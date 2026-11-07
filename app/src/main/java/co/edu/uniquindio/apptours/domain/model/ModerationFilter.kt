package co.edu.uniquindio.apptours.domain.model

enum class ModerationFilter(val statuses: Set<PlaceStatus>) {
    PENDIENTES(setOf(PlaceStatus.PENDIENTE)),
    VERIFICADAS(setOf(PlaceStatus.VERIFICADA, PlaceStatus.RESUELTA)),
    RECHAZADAS(setOf(PlaceStatus.RECHAZADA))
}
