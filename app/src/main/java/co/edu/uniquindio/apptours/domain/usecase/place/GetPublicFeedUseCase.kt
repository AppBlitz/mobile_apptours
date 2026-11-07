package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.util.DistanceCalculator
import co.edu.uniquindio.apptours.domain.model.FeedFilter
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Feed público: solo verificadas/resueltas, filtradas y ordenadas por relevancia (votos). */
class GetPublicFeedUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(filter: FeedFilter): Flow<List<Place>> =
        placeRepository.observePublicPlaces().map { places ->
            places
                .filter { it.status.isPublic && it.matches(filter) }
                .sortedByDescending { it.votesCount }
        }

    private fun Place.matches(filter: FeedFilter): Boolean {
        val query = filter.query.trim()
        val matchesQuery = query.isEmpty() ||
                title.contains(query, ignoreCase = true) ||
                description.contains(query, ignoreCase = true)
        val matchesCategory = filter.category == null || category == filter.category
        val matchesDistance = !filter.onlyNearby || DistanceCalculator.distanceKm(
            filter.userLatitude, filter.userLongitude, latitude, longitude
        ) <= FeedFilter.NEARBY_RADIUS_KM
        return matchesQuery && matchesCategory && matchesDistance
    }
}
