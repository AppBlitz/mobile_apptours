package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.NotificationArgs
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.RecordModerationDecisionUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/**
 * El autor (U-09) o cualquier moderador (M-04) marca la publicación como resuelta.
 * Ambos solo pueden finalizar algo ya verificado: si se pudiera finalizar una pendiente o rechazada,
 * aparecería en el feed público sin pasar por la lista de verificación del moderador.
 */
class MarkPlaceResolvedUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val recordDecision: RecordModerationDecisionUseCase,
    private val awardPoints: AwardPointsUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String): Result<Unit> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        val isModerator = user.role == UserRole.MODERATOR
        if (place.authorId != user.id && !isModerator) {
            return Result.failure(AppException.NotAllowed)
        }
        if (place.status == PlaceStatus.RESUELTA) return Result.success(Unit)
        if (place.status != PlaceStatus.VERIFICADA) {
            return Result.failure(AppException.NotAllowed)
        }

        // El moderador deja registro, XP y trofeos (M-06); el autor solo cambia el estado.
        val result = if (isModerator) {
            recordDecision(user, place, ModerationAction.RESOLVED)
        } else {
            placeRepository.updateStatus(id = placeId, status = PlaceStatus.RESUELTA)
        }
        return result.onSuccess {
            awardPoints(place.authorId, PointAction.PLACE_RESOLVED)
            if (place.authorId != user.id) {
                sendNotification(
                    userId = place.authorId,
                    type = NotificationType.RESOLVED,
                    args = mapOf(
                        NotificationArgs.PLACE_TITLE to place.title,
                        NotificationArgs.POINTS to PointAction.PLACE_RESOLVED.points.toString()
                    ),
                    relatedPlaceId = place.id
                )
            }
        }
    }
}
