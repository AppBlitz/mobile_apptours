package co.edu.uniquindio.apptours.domain.model

/** Lugar marcado como "Visitado" por un usuario (T-05). */
data class Visit(
    val placeId: String,
    val category: PlaceCategory,
    val latitude: Double,
    val longitude: Double,
    val visitedAt: Long = System.currentTimeMillis()
)
