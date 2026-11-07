package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeForUser(userId: String): Flow<List<AppNotification>>
    suspend fun create(notification: AppNotification): Result<Unit>
    suspend fun setRead(id: String, isRead: Boolean): Result<Unit>
    suspend fun markAllRead(userId: String): Result<Unit>
}
