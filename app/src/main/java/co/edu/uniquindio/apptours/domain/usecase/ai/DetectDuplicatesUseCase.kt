package co.edu.uniquindio.apptours.domain.usecase.ai

import co.edu.uniquindio.apptours.core.util.DistanceCalculator
import co.edu.uniquindio.apptours.domain.model.DuplicateCandidate
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckCandidate
import co.edu.uniquindio.apptours.domain.model.DuplicateCheckInput
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.DuplicateDetectionRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Busca publicaciones que podrían ser el mismo lugar antes de publicar (IA-01):
 * 1. Prefiltra en la app los lugares no rechazados a menos de [RADIUS_KM] (máximo [MAX_CANDIDATES], los más cercanos).
 * 2. Si no hay candidatos, no llama al modelo.
 * 3. Le envía al modelo el lugar nuevo y los candidatos y recibe JSON estricto.
 * 4. Descarta ids que no estaban en la lista y similitudes menores a [MIN_SIMILARITY].
 * Un fallo (sin red, cuota agotada) se devuelve como failure para que la UI avise sin bloquear.
 */
class DetectDuplicatesUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val duplicateRepository: DuplicateDetectionRepository
) {
    suspend operator fun invoke(
        input: DuplicateCheckInput,
        excludePlaceId: String? = null
    ): Result<List<DuplicateCandidate>> {
        val nearby = placeRepository.observeAll().first()
            .asSequence()
            .filter { it.status != PlaceStatus.RECHAZADA && it.id != excludePlaceId }
            .map { it to DistanceCalculator.distanceKm(input.latitude, input.longitude, it.latitude, it.longitude) }
            .filter { (_, distance) -> distance <= RADIUS_KM }
            .sortedBy { (_, distance) -> distance }
            .take(MAX_CANDIDATES)
            .toList()
        if (nearby.isEmpty()) return Result.success(emptyList())

        val candidates = nearby.map { (place, distance) ->
            DuplicateCheckCandidate(place.id, place.title, place.description, place.category, distance)
        }
        val byId = nearby.associateBy { (place, _) -> place.id }
        return duplicateRepository.findDuplicates(input, candidates).map { matches ->
            matches
                .filter { it.similarity >= MIN_SIMILARITY }
                .mapNotNull { match ->
                    byId[match.id]?.let { (place, distance) ->
                        DuplicateCandidate(place, distance, match.similarity.coerceIn(0.0, 1.0), match.reason)
                    }
                }
                .distinctBy { it.place.id }
                .sortedByDescending { it.similarity }
        }
    }

    companion object {
        const val RADIUS_KM = 1.0
        const val MAX_CANDIDATES = 20
        const val MIN_SIMILARITY = 0.7
    }
}
