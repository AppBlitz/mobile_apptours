package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.Badge
import co.edu.uniquindio.apptours.domain.model.BadgeId
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.Visit
import java.util.Calendar

/**
 * Progreso de cada insignia a partir de datos reales (U-20). Es una función pura: la usan el perfil
 * (para mostrar el progreso) y [EvaluateBadgesUseCase] (para guardar las recién ganadas).
 */
internal object BadgeCalculator {

    fun calculate(
        user: User,
        ownPlaces: List<Place>,
        visits: List<Visit>,
        publicPlaces: List<Place>,
        now: Long = System.currentTimeMillis()
    ): List<Badge> {
        val verified = ownPlaces.count { it.status.isPublic }
        val featured = if (isFeaturedOfMonth(user.id, publicPlaces, now)) 1 else 0
        return BadgeId.entries.map { id ->
            val progress = when (id) {
                BadgeId.FIRST_PLACE -> ownPlaces.size
                BadgeId.FIRST_COMMENT, BadgeId.COMMENTATOR -> user.commentsCount
                BadgeId.TEN_VERIFIED -> verified
                BadgeId.EXPLORER, BadgeId.ADVENTURER, BadgeId.AMBASSADOR -> user.points
                BadgeId.FEATURED_OF_MONTH -> featured
                BadgeId.TRAVELER -> visits.size
                else -> visits.count { it.category == id.category }
            }
            Badge(id = id, progress = progress.coerceAtMost(id.target), earnedAt = user.earnedBadges[id.name])
        }
    }

    /** La publicación pública con más votos entre las creadas este mes es del usuario (y tiene votos). */
    private fun isFeaturedOfMonth(userId: String, publicPlaces: List<Place>, now: Long): Boolean {
        val startOfMonth = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val top = publicPlaces
            .filter { it.createdAt >= startOfMonth && it.votesCount > 0 }
            .maxByOrNull { it.votesCount }
        return top?.authorId == userId
    }
}
