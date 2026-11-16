package co.edu.uniquindio.apptours.domain.usecase.recommendation

import co.edu.uniquindio.apptours.domain.model.RecommendationResult
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.GetMyVisitsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

/**
 * Recomendaciones del usuario actual (N-01). Escucha sus visitas y los lugares públicos en tiempo real,
 * así que se recalculan solas al marcar o desmarcar "Visitado".
 * [deviceLocation] es la ubicación del teléfono si se conoce; si no, se usa la dirección guardada.
 */
class GetRecommendationsUseCase @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val getMyVisits: GetMyVisitsUseCase,
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(deviceLocation: Flow<Pair<Double, Double>?>): Flow<RecommendationResult> =
        combine(
            getCurrentUser().filterNotNull(),
            getMyVisits(),
            placeRepository.observePublicPlaces(),
            deviceLocation
        ) { user, visits, places, device ->
            RecommendationEngine.recommend(
                userId = user.id,
                userLatitude = device?.first ?: user.latitude,
                userLongitude = device?.second ?: user.longitude,
                visits = visits,
                publicPlaces = places
            )
        }
}
