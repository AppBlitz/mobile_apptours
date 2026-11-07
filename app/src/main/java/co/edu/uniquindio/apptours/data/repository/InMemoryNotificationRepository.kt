package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-2): reemplazar por FirestoreNotificationRepository. */
@Singleton
class InMemoryNotificationRepository @Inject constructor() : NotificationRepository {

    private val notifications = MutableStateFlow(InMemoryDataSource.notifications)

    override fun observeForUser(userId: String): Flow<List<AppNotification>> =
        notifications.map { list -> list.filter { it.userId == userId } }

    override suspend fun create(notification: AppNotification): Result<Unit> {
        val id = notification.id.ifBlank { "notif_${UUID.randomUUID().toString().take(8)}" }
        notifications.update { listOf(notification.copy(id = id)) + it }
        return Result.success(Unit)
    }

    override suspend fun setRead(id: String, isRead: Boolean): Result<Unit> {
        notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = isRead) else it }
        }
        return Result.success(Unit)
    }

    override suspend fun markAllRead(userId: String): Result<Unit> {
        notifications.update { list ->
            list.map { if (it.userId == userId) it.copy(isRead = true) else it }
        }
        return Result.success(Unit)
    }
}
