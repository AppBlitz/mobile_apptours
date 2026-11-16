package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.NotificationDto
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Notificaciones en la nube (R-04). El orden lo pone el caso de uso, así no hace falta índice compuesto. */
@Singleton
class FirestoreNotificationRepository @Inject constructor(
    private val db: FirebaseFirestore
) : NotificationRepository {

    private val notifications = db.collection(FirestoreCollections.NOTIFICATIONS)

    override fun observeForUser(userId: String): Flow<List<AppNotification>> =
        notifications.whereEqualTo("userId", userId).observe { NotificationDto.from(it).toDomain() }

    override suspend fun create(notification: AppNotification): Result<Unit> = firestoreCall {
        val ref = if (notification.id.isBlank()) {
            notifications.document()
        } else {
            notifications.document(notification.id)
        }
        ref.set(notification.toDto().toMap()).await()
        Unit
    }

    override suspend fun setRead(id: String, isRead: Boolean): Result<Unit> = firestoreCall {
        notifications.document(id).update("isRead", isRead).await()
        Unit
    }

    override suspend fun markAllRead(userId: String): Result<Unit> = firestoreCall {
        val unread = notifications
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()
        if (unread.isEmpty) return@firestoreCall
        val batch = db.batch()
        unread.documents.forEach { batch.update(it.reference, "isRead", true) }
        batch.commit().await()
    }
}
