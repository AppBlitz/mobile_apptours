package co.edu.uniquindio.apptours.features.user.feed

import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

data class FeedUiState(
    val firstName: String = "",
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE,
    val query: String = "",
    val selectedCategory: PlaceCategory? = null,
    val onlyNearby: Boolean = true,
    val places: List<Place> = emptyList(),
    val selectedMapPlaceId: String? = null,
    val votedPlaceIds: Set<String> = emptySet(),
    val visitedPlaceIds: Set<String> = emptySet()
)
