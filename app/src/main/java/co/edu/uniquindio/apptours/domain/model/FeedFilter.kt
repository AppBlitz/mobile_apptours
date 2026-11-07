package co.edu.uniquindio.apptours.domain.model

data class FeedFilter(
    val query: String = "",
    val category: PlaceCategory? = null,
    val onlyNearby: Boolean = true,
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE
) {
    companion object {
        const val NEARBY_RADIUS_KM = 10.0
    }
}
