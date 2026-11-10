package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.NotificationArgs
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/**
 * "Me interesa" (U-10): un voto por usuario. Al votar un lugar ajeno el autor gana puntos y
 * recibe notificación; el voto propio no suma puntos ni notifica.
 */
class ToggleInterestUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val awardPoints: AwardPointsUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String): Result<Boolean> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)

        return placeRepository.toggleVote(placeId, user.id).onSuccess { voted ->
            if (voted && place.authorId != user.id) {
                awardPoints(place.authorId, PointAction.VOTE_RECEIVED)
                sendNotification(
                    userId = place.authorId,
                    type = NotificationType.VOTE,
                    args = mapOf(
                        NotificationArgs.ACTOR_NAME to user.fullName,
                        NotificationArgs.PLACE_TITLE to place.title,
                        NotificationArgs.POINTS to PointAction.VOTE_RECEIVED.points.toString()
                    ),
                    relatedPlaceId = place.id
                )
            }
        }
    }
}
