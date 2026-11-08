package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.PlaceDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePlaceRepository @Inject constructor(
    private val db: FirebaseFirestore
) : PlaceRepository {

    private val places = db.collection(FirestoreCollections.PLACES)

    private fun votedPlaces(userId: String) = db.collection(FirestoreCollections.USERS)
        .document(userId)
        .collection(FirestoreCollections.VOTED_PLACES)

    override fun observeAll(): Flow<List<Place>> =
        places.observe { PlaceDto.from(it).toDomain() }

    override fun observePublicPlaces(): Flow<List<Place>> =
        observeByStatus(PlaceStatus.entries.filter { it.isPublic }.toSet())

    override fun observePlace(id: String): Flow<Place?> =
        places.document(id).observe { PlaceDto.from(it).toDomain() }

    override fun observePlacesByAuthor(authorId: String): Flow<List<Place>> =
        places.whereEqualTo("authorId", authorId).observe { PlaceDto.from(it).toDomain() }

    override fun observeByStatus(statuses: Set<PlaceStatus>): Flow<List<Place>> {
        if (statuses.isEmpty()) return flowOf(emptyList())
        return places.whereIn("status", statuses.map { it.name })
            .observe { PlaceDto.from(it).toDomain() }
    }

    override fun observeReviewedBy(moderatorId: String): Flow<List<Place>> =
        places.whereEqualTo("moderatorId", moderatorId).observe { PlaceDto.from(it).toDomain() }

    override suspend fun getPlace(id: String): Place? = firestoreCall {
        places.document(id).get().await().takeIf { it.exists() }?.let { PlaceDto.from(it).toDomain() }
    }.getOrNull()

    override suspend fun create(place: Place): Result<String> = firestoreCall {
        val ref = if (place.id.isBlank()) places.document() else places.document(place.id)
        ref.set(place.toDto().toMap()).await()
        ref.id
    }

    override suspend fun update(place: Place): Result<Unit> = firestoreCall {
        val ref = places.document(place.id)
        if (!ref.get().await().exists()) throw AppException.PlaceNotFound
        ref.update(place.toDto().editableFields()).await()
        Unit
    }

    /** Borra la publicación con sus comentarios y votos (Firestore no borra subcolecciones solo). */
    override suspend fun delete(id: String): Result<Unit> = firestoreCall {
        val ref = places.document(id)
        val votes = ref.collection(FirestoreCollections.VOTES).get().await()
        val comments = ref.collection(FirestoreCollections.COMMENTS).get().await()
        val batch = db.batch()
        votes.documents.forEach { vote ->
            batch.delete(vote.reference)
            batch.delete(votedPlaces(vote.id).document(id))
        }
        comments.documents.forEach { batch.delete(it.reference) }
        batch.delete(ref)
        batch.commit().await()
        Unit
    }

    /** Borra las publicaciones del autor y retira los votos que dio en publicaciones ajenas. */
    override suspend fun deleteByAuthor(authorId: String): Result<Unit> = firestoreCall {
        val own = places.whereEqualTo("authorId", authorId).get().await()
        own.documents.forEach { delete(it.id).getOrThrow() }
        votedPlaces(authorId).get().await().documents.forEach { vote ->
            removeVote(placeId = vote.id, userId = authorId)
        }
    }

    override suspend fun updateStatus(
        id: String,
        status: PlaceStatus,
        reason: String?,
        moderatorId: String?
    ): Result<Unit> = firestoreCall {
        val ref = places.document(id)
        if (!ref.get().await().exists()) throw AppException.PlaceNotFound
        val fields = buildMap<String, Any?> {
            put("status", status.name)
            put("rejectionReason", if (status == PlaceStatus.RECHAZADA) reason else null)
            if (moderatorId != null) {
                put("moderatorId", moderatorId)
                put("reviewedAt", Timestamp.now())
            }
        }
        ref.update(fields).await()
        Unit
    }

    override fun observeVotedPlaceIds(userId: String): Flow<Set<String>> =
        votedPlaces(userId).observe { it.id }.map { it.toSet() }

    /** Voto único por usuario: el voto, su espejo y el contador cambian en una sola transacción. */
    override suspend fun toggleVote(placeId: String, userId: String): Result<Boolean> = firestoreCall {
        val placeRef = places.document(placeId)
        val voteRef = placeRef.collection(FirestoreCollections.VOTES).document(userId)
        val mirrorRef = votedPlaces(userId).document(placeId)
        db.runTransaction { transaction ->
            if (!transaction.get(placeRef).exists()) throw AppException.PlaceNotFound
            val alreadyVoted = transaction.get(voteRef).exists()
            if (alreadyVoted) {
                transaction.delete(voteRef)
                transaction.delete(mirrorRef)
                transaction.update(placeRef, "votesCount", FieldValue.increment(-1))
            } else {
                val vote = mapOf("userId" to userId, "createdAt" to Timestamp.now())
                transaction.set(voteRef, vote)
                transaction.set(mirrorRef, vote)
                transaction.update(placeRef, "votesCount", FieldValue.increment(1))
            }
            !alreadyVoted
        }.await()
    }

    private suspend fun removeVote(placeId: String, userId: String) {
        val placeRef = places.document(placeId)
        val voteRef = placeRef.collection(FirestoreCollections.VOTES).document(userId)
        val mirrorRef = votedPlaces(userId).document(placeId)
        db.runTransaction { transaction ->
            val placeExists = transaction.get(placeRef).exists()
            val voted = transaction.get(voteRef).exists()
            if (placeExists && voted) {
                transaction.update(placeRef, "votesCount", FieldValue.increment(-1))
            }
            transaction.delete(voteRef)
            transaction.delete(mirrorRef)
        }.await()
    }
}
