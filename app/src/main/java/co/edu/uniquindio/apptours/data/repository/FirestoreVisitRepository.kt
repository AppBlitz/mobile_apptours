package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.VisitDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreVisitRepository @Inject constructor(
    private val db: FirebaseFirestore
) : VisitRepository {

    private fun visits(userId: String) = db.collection(FirestoreCollections.USERS)
        .document(userId)
        .collection(FirestoreCollections.VISITS)

    private fun place(placeId: String) = db.collection(FirestoreCollections.PLACES).document(placeId)

    override fun observeVisits(userId: String): Flow<List<Visit>> =
        visits(userId).observe { VisitDto.from(it).toDomain() }

    /** La visita y el contador `visitsCount` del lugar cambian en una sola transacción. */
    override suspend fun toggleVisit(userId: String, visit: Visit): Result<Boolean> = firestoreCall {
        val visitRef = visits(userId).document(visit.placeId)
        val placeRef = place(visit.placeId)
        db.runTransaction { transaction ->
            if (!transaction.get(placeRef).exists()) throw AppException.PlaceNotFound
            val alreadyVisited = transaction.get(visitRef).exists()
            if (alreadyVisited) {
                transaction.delete(visitRef)
                transaction.update(placeRef, "visitsCount", FieldValue.increment(-1))
            } else {
                transaction.set(visitRef, visit.toDto().toMap())
                transaction.update(placeRef, "visitsCount", FieldValue.increment(1))
            }
            !alreadyVisited
        }.await()
    }

    override suspend fun saveExperience(
        userId: String,
        placeId: String,
        experience: String?,
        rating: Int
    ): Result<Unit> = firestoreCall {
        visits(userId).document(placeId)
            .update(mapOf("experience" to experience, "rating" to rating))
            .await()
        Unit
    }

    /** Al borrar la cuenta se quitan sus visitas y se descuentan de cada lugar que aún exista. */
    override suspend fun deleteAllForUser(userId: String): Result<Unit> = firestoreCall {
        visits(userId).get().await().documents.forEach { doc ->
            val placeRef = place(doc.id)
            db.runTransaction { transaction ->
                val placeExists = transaction.get(placeRef).exists()
                if (placeExists) transaction.update(placeRef, "visitsCount", FieldValue.increment(-1))
                transaction.delete(doc.reference)
            }.await()
        }
    }
}
