package co.edu.uniquindio.apptours.domain.model

/** Lugar marcado como "Visitado" por un usuario (T-05), con su experiencia opcional. */
data class Visit(
    val placeId: String,
    val category: PlaceCategory,
    val latitude: Double,
    val longitude: Double,
    val visitedAt: Long = System.currentTimeMillis(),
    val experience: String? = null,
    val rating: Int? = null
)
