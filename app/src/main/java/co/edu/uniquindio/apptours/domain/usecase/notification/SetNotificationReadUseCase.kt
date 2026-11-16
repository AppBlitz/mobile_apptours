package co.edu.uniquindio.apptours.domain.usecase.notification

import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import javax.inject.Inject

class SetNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String, isRead: Boolean = true): Result<Unit> =
        notificationRepository.setRead(notificationId, isRead)
}
