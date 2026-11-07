package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/** Verificar (M-02): la publicación pasa al feed público, el autor gana puntos y es notificado. */
class VerifyPlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val awardPoints: AwardPointsUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String): Result<Unit> {
        val moderator = getSession.requireModerator().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)

        return placeRepository.updateStatus(
            id = placeId,
            status = PlaceStatus.VERIFICADA,
            moderatorId = moderator.id
        ).onSuccess {
            awardPoints(place.authorId, PointAction.PLACE_VERIFIED)
            sendNotification(
                userId = place.authorId,
                type = NotificationType.VERIFIED,
                title = "Un moderador verificó tu publicación «${place.title}»",
                body = "Ya es visible en el feed público. Ganaste +${PointAction.PLACE_VERIFIED.points} puntos.",
                relatedPlaceId = place.id
            )
        }
    }
}
