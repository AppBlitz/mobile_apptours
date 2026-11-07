package co.edu.uniquindio.apptours.domain.usecase.notification

import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Crea una notificación para un usuario.
 * TODO(IT-11): guardar tipo + argumentos y construir el texto en la UI según el idioma.
 */
class SendNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: NotificationType,
        title: String,
        body: String,
        relatedPlaceId: String? = null
    ): Result<Unit> = notificationRepository.create(
        AppNotification(
            id = "",
            userId = userId,
            type = type,
            title = title,
            body = body,
            relatedPlaceId = relatedPlaceId
        )
    )
}
