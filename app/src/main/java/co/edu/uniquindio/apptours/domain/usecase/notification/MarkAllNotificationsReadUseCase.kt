package co.edu.uniquindio.apptours.domain.usecase.notification

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsReadUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val uid = authRepository.currentUserId()
            ?: return Result.failure(AppException.NotAuthenticated)
        return notificationRepository.markAllRead(uid)
    }
}
