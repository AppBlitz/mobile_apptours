package co.edu.uniquindio.apptours.features.user.feed

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.LocationFilter
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

data class FeedUiState(
    val firstName: String = "",
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE,
    /** true si las distancias se miden desde donde está el teléfono y no desde la dirección guardada. */
    val usingDeviceLocation: Boolean = false,
    val query: String = "",
    val selectedCategory: PlaceCategory? = null,
    val location: LocationFilter = LocationFilter.TODAS,
    val places: List<Place> = emptyList(),
    val selectedMapPlaceId: String? = null,
    val votedPlaceIds: Set<String> = emptySet(),
    val visitedPlaceIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)
