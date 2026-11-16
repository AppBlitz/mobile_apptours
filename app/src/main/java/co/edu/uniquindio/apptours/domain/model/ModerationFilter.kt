package co.edu.uniquindio.apptours.domain.model

/** Filtros de la cola de moderación: uno por estado. */
enum class ModerationFilter(val statuses: Set<PlaceStatus>) {
    PENDIENTES(setOf(PlaceStatus.PENDIENTE)),
    VERIFICADAS(setOf(PlaceStatus.VERIFICADA)),
    RECHAZADAS(setOf(PlaceStatus.RECHAZADA)),
    RESUELTAS(setOf(PlaceStatus.RESUELTA))
}
