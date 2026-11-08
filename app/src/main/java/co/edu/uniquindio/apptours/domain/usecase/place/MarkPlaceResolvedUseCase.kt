package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/**
 * El autor (U-09) o cualquier moderador (M-04) marca la publicación como resuelta.
 * El autor solo puede finalizar algo ya verificado: si pudiera finalizar una pendiente o rechazada,
 * aparecería en el feed público sin pasar por moderación.
 */
class MarkPlaceResolvedUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
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
        if (!isModerator && place.status != PlaceStatus.VERIFICADA) {
            return Result.failure(AppException.NotAllowed)
        }

        return placeRepository.updateStatus(
            id = placeId,
            status = PlaceStatus.RESUELTA,
            moderatorId = if (isModerator) user.id else null
        ).onSuccess {
            awardPoints(place.authorId, PointAction.PLACE_RESOLVED)
            if (place.authorId != user.id) {
                sendNotification(
                    userId = place.authorId,
                    type = NotificationType.RESOLVED,
                    title = "Un moderador marcó como resuelta tu publicación «${place.title}»",
                    body = "Sumaste +${PointAction.PLACE_RESOLVED.points} puntos.",
                    relatedPlaceId = place.id
                )
            }
        }
    }
}
