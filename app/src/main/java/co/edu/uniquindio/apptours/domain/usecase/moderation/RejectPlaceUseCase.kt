package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import co.edu.uniquindio.apptours.domain.validation.ModerationForms
import javax.inject.Inject

/** Rechazar (M-03): el motivo es obligatorio (15–300 caracteres) y se notifica al autor con él. */
class RejectPlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val recordDecision: RecordModerationDecisionUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String, reason: String): Result<Unit> {
        val errors = ModerationForms.rejection(reason)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))
        val moderator = getSession.requireModerator().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        if (place.status == PlaceStatus.RECHAZADA) return Result.success(Unit)
        val cleanReason = reason.trim()

        return recordDecision(moderator, place, ModerationAction.REJECTED, cleanReason).onSuccess {
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
