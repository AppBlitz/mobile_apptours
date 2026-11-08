package co.edu.uniquindio.apptours.domain.usecase.visit

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * "Comparte tu experiencia" (T-05): guarda la calificación y el texto en la visita y, si hay texto,
 * lo publica como comentario marcado "Visitó este lugar". La primera experiencia de cada visita
 * suma los puntos de [PointAction.VISIT_EXPERIENCE]; editarla después no vuelve a sumar.
 */
class ShareVisitExperienceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val visitRepository: VisitRepository,
    private val addComment: AddCommentUseCase,
    private val awardPoints: AwardPointsUseCase,
    private val evaluateBadges: EvaluateBadgesUseCase
) {
    suspend operator fun invoke(placeId: String, text: String, rating: Int?): Result<Unit> {
        val errors = PlaceForms.experience(rating, text)
        if (errors.isNotEmpty() || rating == null) return Result.failure(AppException.InvalidFields(errors))
        val user = getSession.require().getOrElse { return Result.failure(it) }

        val visit = runCatching { visitRepository.observeVisits(user.id).first() }
            .getOrNull()
            ?.find { it.placeId == placeId }
        val isFirstExperience = visit?.rating == null

        val experience = text.trim().ifEmpty { null }
        visitRepository.saveExperience(user.id, placeId, experience, rating)
            .onFailure { return Result.failure(it) }
        if (isFirstExperience) awardPoints(user.id, PointAction.VISIT_EXPERIENCE)

        return if (experience == null) {
            evaluateBadges()
            Result.success(Unit)
        } else {
            addComment(placeId, experience, visitRating = rating)
        }
    }
}
