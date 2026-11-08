package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.VisitDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
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

    override fun observeVisits(userId: String): Flow<List<Visit>> =
        visits(userId).observe { VisitDto.from(it).toDomain() }

    override suspend fun toggleVisit(userId: String, visit: Visit): Result<Boolean> = firestoreCall {
        val ref = visits(userId).document(visit.placeId)
        val alreadyVisited = ref.get().await().exists()
        if (alreadyVisited) {
            ref.delete().await()
        } else {
            ref.set(visit.toDto().toMap()).await()
        }
        !alreadyVisited
    }

    override suspend fun deleteAllForUser(userId: String): Result<Unit> = firestoreCall {
        val batch = db.batch()
        visits(userId).get().await().documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
        Unit
    }
}
