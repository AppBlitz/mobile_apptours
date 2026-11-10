package co.edu.uniquindio.apptours.domain.model

/** Lugar que el usuario quiere publicar, tal como se le envía al detector de duplicados (IA-01). */
data class DuplicateCheckInput(
    val title: String,
    val description: String,
    val category: PlaceCategory?,
    val latitude: Double,
    val longitude: Double
)

/** Lugar cercano ya publicado que podría ser el mismo. */
data class DuplicateCheckCandidate(
    val id: String,
    val title: String,
    val description: String,
    val category: PlaceCategory,
    val distanceKm: Double
)

/** Respuesta del modelo para un candidato. */
data class DuplicateMatch(
    val id: String,
    val similarity: Double,
    val reason: String
)

/** Posible duplicado que se le muestra al usuario antes de publicar. */
data class DuplicateCandidate(
    val place: Place,
    val distanceKm: Double,
    val similarity: Double,
    val reason: String
)
