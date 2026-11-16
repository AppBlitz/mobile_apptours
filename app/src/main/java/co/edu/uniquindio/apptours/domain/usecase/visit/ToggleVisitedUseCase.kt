package co.edu.uniquindio.apptours.domain.usecase.visit

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import javax.inject.Inject

/** Marca o desmarca un lugar como "Visitado" (T-05). Devuelve true si quedó visitado. */
class ToggleVisitedUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository,
    private val evaluateBadges: EvaluateBadgesUseCase
) {
    suspend operator fun invoke(placeId: String): Result<Boolean> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        return visitRepository.toggleVisit(
            userId = user.id,
            visit = Visit(
                placeId = place.id,
                category = place.category,
                latitude = place.latitude,
                longitude = place.longitude
            )
        ).onSuccess { visited ->
            // Viajero y "Amante de…" dependen de las visitas.
            if (visited) evaluateBadges()
        }
    }
}
