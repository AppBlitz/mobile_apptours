package co.edu.uniquindio.apptours.domain.model

/**
 * Filtro de ubicación del feed (U-06), medido desde la ubicación guardada del usuario.
 * "En mi ciudad" usa un radio que cubre el casco urbano de Armenia y sus alrededores;
 * cuando el mapa real (IT-7) entregue la ciudad de cada punto, se puede comparar por nombre.
 */
enum class LocationFilter(val radiusKm: Double?) {
    CERCANOS(5.0),
    CIUDAD(15.0),
    TODAS(null)
}

data class FeedFilter(
    val query: String = "",
    val category: PlaceCategory? = null,
    val location: LocationFilter = LocationFilter.TODAS,
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE
)
