package co.edu.uniquindio.apptours.domain.usecase.comment

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.CommentRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import javax.inject.Inject

/**
 * Comentar (U-11): suma puntos y notifica al autor cada vez (U-15), salvo que se comente a sí mismo.
 * Con [visitRating] el comentario es una experiencia de visita ("Visitó este lugar", T-05).
 */
class AddCommentUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val awardPoints: AwardPointsUseCase,
    private val evaluateBadges: EvaluateBadgesUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(placeId: String, text: String, visitRating: Int? = null): Result<Unit> {
        val errors = if (visitRating == null) PlaceForms.comment(text) else PlaceForms.experience(visitRating, text)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)

        val comment = Comment(
            id = "",
            placeId = placeId,
            authorId = user.id,
            authorName = user.fullName,
            authorLevel = user.level,
            text = text.trim(),
            isVisitExperience = visitRating != null,
            rating = visitRating
        )
        return commentRepository.add(comment).map {
            userRepository.incrementCommentsCount(user.id)
            // La experiencia de visita tiene su propio puntaje (ShareVisitExperienceUseCase).
            if (visitRating == null) awardPoints(user.id, PointAction.COMMENT)
            evaluateBadges()
            if (place.authorId != user.id) {
                sendNotification(
                    userId = place.authorId,
                    type = NotificationType.COMMENT,
                    title = "${user.fullName} comentó en «${place.title}»",
                    body = comment.text,
                    relatedPlaceId = place.id
                )
            }
        }
    }
}
