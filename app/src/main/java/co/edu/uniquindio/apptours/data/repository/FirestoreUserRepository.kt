package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.UserDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toTimestamp
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserLevel
import co.edu.uniquindio.apptours.domain.repository.PointsChange
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserRepository @Inject constructor(
    private val db: FirebaseFirestore
) : UserRepository {

    private val users = db.collection(FirestoreCollections.USERS)

    override fun observeUser(id: String): Flow<User?> =
        users.document(id).observe { UserDto.from(it).toDomain() }

    override suspend fun getUser(id: String): User? = firestoreCall {
        users.document(id).get().await().takeIf { it.exists() }?.let { UserDto.from(it).toDomain() }
    }.getOrNull()

    override suspend fun create(user: User): Result<Unit> = firestoreCall {
        users.document(user.id).set(user.toDto().toMap()).await()
        Unit
    }

    /** Solo actualiza los datos del perfil; el rol y los puntos no se tocan desde aquí. */
    override suspend fun update(user: User): Result<Unit> = firestoreCall {
        val ref = users.document(user.id)
        if (!ref.get().await().exists()) throw AppException.UserNotFound
        ref.update(user.toDto().profileFields()).await()
        Unit
    }

    override suspend fun delete(id: String): Result<Unit> = firestoreCall {
        users.document(id).delete().await()
        Unit
    }

    /** Transacción: dos sumas simultáneas no se pisan y el nivel guardado siempre coincide con los puntos. */
    override suspend fun addPoints(userId: String, points: Int): Result<PointsChange> = firestoreCall {
        val ref = users.document(userId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            if (!snapshot.exists()) throw AppException.UserNotFound
            val before = snapshot.getLong("points")?.toInt() ?: 0
            val after = before + points
            transaction.update(ref, mapOf("points" to after, "level" to UserLevel.fromPoints(after).name))
            PointsChange(before, after)
        }.await()
    }

    override suspend fun incrementCommentsCount(userId: String): Result<Unit> = firestoreCall {
        users.document(userId).update("commentsCount", FieldValue.increment(1)).await()
        Unit
    }

    override suspend fun addBadges(userId: String, badges: Map<String, Long>): Result<Unit> = firestoreCall {
        if (badges.isEmpty()) return@firestoreCall
        // Notación con punto: agrega claves al mapa sin borrar las insignias que ya tenía.
        val fields = badges.map { (id, earnedAt) -> "badges.$id" to earnedAt.toTimestamp() }.toMap()
        users.document(userId).update(fields).await()
    }
}
