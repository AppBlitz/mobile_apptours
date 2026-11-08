package co.edu.uniquindio.apptours.features.user.placedetail

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place

data class PlaceDetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val comments: List<Comment> = emptyList(),
    val currentUserId: String? = null,
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE,
    val isInterested: Boolean = false,
    val isVisited: Boolean = false,
    val commentText: String = "",
    @StringRes val errorRes: Int? = null,
    @StringRes val loadErrorRes: Int? = null
) {
    val isOwner: Boolean
        get() = place != null && place.authorId == currentUserId
}
