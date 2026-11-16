package co.edu.uniquindio.apptours.domain.usecase.recommendation

import co.edu.uniquindio.apptours.core.util.DistanceCalculator
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.Recommendation
import co.edu.uniquindio.apptours.domain.model.RecommendationReason
import co.edu.uniquindio.apptours.domain.model.RecommendationResult
import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.model.VisitZone

/**
 * Recomendador por historial de visitas (N-01). Algoritmo determinista, sin IA ni red:
 *
 * 1. afinidad(c) = visitas en la categoría c / total de visitas.
 * 2. Zonas frecuentes: las visitas se agrupan por radio ([ZONE_RADIUS_KM]); cada visita se une a la
 *    primera zona cuyo centro esté a menos de ese radio (el centro se recalcula como promedio) o abre
 *    una nueva. Se conservan las [MAX_ZONES] zonas con más visitas.
 * 3. Candidatos: lugares públicos que el usuario no ha visitado y que no publicó él.
 * 4. score = 0.5·afinidad(categoría) + 0.35·proximidad + 0.15·popularidad, con
 *    proximidad = 1 − min(distancia a la zona más cercana / [MAX_RADIUS_KM], 1) y
 *    popularidad = votos / máximo de votos entre los candidatos.
 * 5. Arranque en frío (menos de [MIN_VISITS] visitas): los más votados a menos de [MAX_RADIUS_KM]
 *    de la ubicación del usuario (si no hay ninguno tan cerca, los más votados en general).
 */
object RecommendationEngine {

    const val WEIGHT_AFFINITY = 0.5
    const val WEIGHT_PROXIMITY = 0.35
    const val WEIGHT_POPULARITY = 0.15
    const val ZONE_RADIUS_KM = 2.0
    const val MAX_RADIUS_KM = 10.0
    const val MAX_ZONES = 3
    const val MIN_VISITS = 2
    const val MAX_RESULTS = 10

    fun recommend(
        userId: String,
        userLatitude: Double,
        userLongitude: Double,
        visits: List<Visit>,
        publicPlaces: List<Place>
    ): RecommendationResult {
        val visitedIds = visits.map { it.placeId }.toSet()
        val candidates = publicPlaces.filter {
            it.status.isPublic && it.id !in visitedIds && it.authorId != userId
        }
        val affinity = categoryAffinity(visits)
        val zones = frequentZones(visits)
        fun distanceFromUser(place: Place) =
            DistanceCalculator.distanceKm(userLatitude, userLongitude, place.latitude, place.longitude)

        if (visits.size < MIN_VISITS) {
            val nearby = candidates.filter { distanceFromUser(it) <= MAX_RADIUS_KM }.ifEmpty { candidates }
            val maxVotes = nearby.maxOfOrNull { it.votesCount } ?: 0
            val popular = nearby
                .sortedWith(compareByDescending<Place> { it.votesCount }.thenBy { distanceFromUser(it) })
                .take(MAX_RESULTS)
                .map { place ->
                    Recommendation(
                        place = place,
                        score = popularity(place.votesCount, maxVotes),
                        reason = RecommendationReason.Popular(place.votesCount),
                        distanceKm = distanceFromUser(place)
                    )
                }
            return RecommendationResult(popular, isColdStart = true, visits.size, affinity, zones)
        }

        val maxVotes = candidates.maxOfOrNull { it.votesCount } ?: 0
        val visitsByCategory = visits.groupingBy { it.category }.eachCount()
        val ranked = candidates
            .map { place ->
                val zoneDistance = zones.minOfOrNull {
                    DistanceCalculator.distanceKm(it.latitude, it.longitude, place.latitude, place.longitude)
                }
                val score = WEIGHT_AFFINITY * (affinity[place.category] ?: 0.0) +
                        WEIGHT_PROXIMITY * proximity(zoneDistance) +
                        WEIGHT_POPULARITY * popularity(place.votesCount, maxVotes)
                Recommendation(
                    place = place,
                    score = score,
                    reason = RecommendationReason.History(
                        category = place.category,
                        visitsInCategory = visitsByCategory[place.category] ?: 0,
                        distanceToZoneKm = zoneDistance
                    ),
                    distanceKm = distanceFromUser(place)
                )
            }
            .sortedWith(compareByDescending<Recommendation> { it.score }.thenByDescending { it.place.votesCount })
            .take(MAX_RESULTS)
        return RecommendationResult(ranked, isColdStart = false, visits.size, affinity, zones)
    }

    fun categoryAffinity(visits: List<Visit>): Map<PlaceCategory, Double> {
        if (visits.isEmpty()) return emptyMap()
        return visits.groupingBy { it.category }.eachCount()
            .mapValues { (_, count) -> count.toDouble() / visits.size }
    }

    fun frequentZones(visits: List<Visit>): List<VisitZone> {
        val groups = mutableListOf<MutableList<Visit>>()
        val centers = mutableListOf<Pair<Double, Double>>()
        for (visit in visits.sortedBy { it.visitedAt }) {
            val index = centers.indexOfFirst { (lat, lng) ->
                DistanceCalculator.distanceKm(lat, lng, visit.latitude, visit.longitude) <= ZONE_RADIUS_KM
            }
            if (index == -1) {
                groups += mutableListOf(visit)
                centers += visit.latitude to visit.longitude
            } else {
                val group = groups[index].apply { add(visit) }
                centers[index] = group.map { it.latitude }.average() to group.map { it.longitude }.average()
            }
        }
        return groups.indices
            .map { VisitZone(centers[it].first, centers[it].second, groups[it].size) }
            .sortedByDescending { it.visits }
            .take(MAX_ZONES)
    }

    fun proximity(distanceToZoneKm: Double?): Double =
        if (distanceToZoneKm == null) 0.0 else 1.0 - (distanceToZoneKm / MAX_RADIUS_KM).coerceAtMost(1.0)

    fun popularity(votes: Int, maxVotes: Int): Double =
        if (maxVotes <= 0) 0.0 else votes.toDouble() / maxVotes
}
