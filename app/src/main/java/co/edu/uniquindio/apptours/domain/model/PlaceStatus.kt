package co.edu.uniquindio.apptours.domain.model

enum class PlaceStatus {
    PENDIENTE,
    VERIFICADA,
    RECHAZADA,
    RESUELTA;

    /** Solo las verificadas y resueltas aparecen en el feed público. */
    val isPublic: Boolean
        get() = this == VERIFICADA || this == RESUELTA
}
