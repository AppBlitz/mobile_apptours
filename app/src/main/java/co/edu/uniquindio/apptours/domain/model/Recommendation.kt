package co.edu.uniquindio.apptours.domain.model

/** Lugar recomendado (N-01) con su puntaje y el porqué, para mostrar la justificación en la UI. */
data class Recommendation(
    val place: Place,
    /** Puntaje final de 0 a 1. */
    val score: Double,
    val reason: RecommendationReason,
    /** Distancia del lugar a la ubicación del usuario. */
    val distanceKm: Double
)

sealed interface RecommendationReason {
    /**
     * Por historial: el usuario visitó [visitsInCategory] lugares de [category] y el lugar está a
     * [distanceToZoneKm] km de la zona que más frecuenta cerca de él (null si no hay zonas).
     */
    data class History(
        val category: PlaceCategory,
        val visitsInCategory: Int,
        val distanceToZoneKm: Double?
    ) : RecommendationReason

    /** Arranque en frío: de los más votados cerca de la ubicación del usuario. */
    data class Popular(val votes: Int) : RecommendationReason
}

/** Zona frecuente: centro de un grupo de visitas cercanas entre sí. */
data class VisitZone(
    val latitude: Double,
    val longitude: Double,
    val visits: Int
)

/** Resultado completo del recomendador, con los datos que explican el cálculo. */
data class RecommendationResult(
    val recommendations: List<Recommendation>,
    /** true si el usuario tiene menos de 2 visitas y se muestran los más votados cerca. */
    val isColdStart: Boolean,
    val totalVisits: Int,
    /** afinidad(c) = visitas en c / total de visitas. */
    val categoryAffinity: Map<PlaceCategory, Double>,
    val zones: List<VisitZone>
)
