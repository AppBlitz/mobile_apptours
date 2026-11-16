package co.edu.uniquindio.apptours.domain.usecase.notification

import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * Crea una notificación para un usuario. Solo guarda el tipo y los datos ([args], claves en
 * [co.edu.uniquindio.apptours.domain.model.NotificationArgs]); el texto lo arma la UI según el idioma.
 */
class SendNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: NotificationType,
        args: Map<String, String> = emptyMap(),
        relatedPlaceId: String? = null
    ): Result<Unit> = notificationRepository.create(
        AppNotification(
            id = "",
            userId = userId,
            type = type,
            args = args,
            relatedPlaceId = relatedPlaceId
        )
    )
}
