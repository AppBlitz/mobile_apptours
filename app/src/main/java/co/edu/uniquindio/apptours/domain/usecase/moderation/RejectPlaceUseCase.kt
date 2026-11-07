package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/** Rechazar (M-03): el motivo es obligatorio y se notifica al autor. */
class RejectPlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String, reason: String): Result<Unit> {
        if (reason.isBlank()) {
            return Result.failure(AppException.InvalidFields(setOf(FormField.REJECTION_REASON)))
        }
        val moderator = getSession.requireModerator().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        val cleanReason = reason.trim()

        return placeRepository.updateStatus(
            id = placeId,
            status = PlaceStatus.RECHAZADA,
            reason = cleanReason,
            moderatorId = moderator.id
        ).onSuccess {
            sendNotification(
                userId = place.authorId,
                type = NotificationType.REJECTED,
                title = "Un moderador rechazó tu publicación «${place.title}»",
                body = "Motivo: $cleanReason",
                relatedPlaceId = place.id
            )
        }
    }
}
