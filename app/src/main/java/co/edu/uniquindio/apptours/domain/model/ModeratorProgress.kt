package co.edu.uniquindio.apptours.domain.model

/** Acciones de moderación y la XP que da cada una (M-06). Tabla única: la usa también el perfil. */
enum class ModerationAction(val xp: Int, val resultingStatus: PlaceStatus) {
    VERIFIED(15, PlaceStatus.VERIFICADA),
    REJECTED(15, PlaceStatus.RECHAZADA),
    RESOLVED(5, PlaceStatus.RESUELTA)
}

/** Bonificación por revisar [DAILY_BONUS_REVIEWS] publicaciones en un mismo día. */
object ModeratorXp {
    const val DAILY_BONUS = 50
    const val DAILY_BONUS_REVIEWS = 10

    /** Máximo que puede sumar una sola acción (la mayor XP + la bonificación); lo usan las reglas. */
    const val MAX_PER_ACTION = 15 + DAILY_BONUS
}

/** Entrada de `moderationLog`: historial permanente de lo que hizo cada moderador. */
data class ModerationLogEntry(
    val id: String,
    val moderatorId: String,
    val placeId: String,
    val placeTitle: String,
    val action: ModerationAction,
    val reason: String? = null,
    val xpAwarded: Int,
    val createdAt: Long = System.currentTimeMillis()
)

/** Contadores, XP y trofeos del moderador (`moderatorStats/{uid}`). */
data class ModeratorStats(
    val xp: Int = 0,
    val reviewedCount: Int = 0,
    val verifiedCount: Int = 0,
    val rejectedCount: Int = 0,
    val resolvedCount: Int = 0,
    /** id de [TrophyId] → fecha real de obtención (millis). */
    val trophies: Map<String, Long> = emptyMap()
) {
    /** Porcentaje de rechazos entre lo verificado y lo rechazado (0 si aún no hay decisiones). */
    val rejectionRate: Int
        get() {
            val decided = verifiedCount + rejectedCount
            return if (decided == 0) 0 else (rejectedCount * 100 / decided)
        }

    fun after(action: ModerationAction, xpAwarded: Int): ModeratorStats = copy(
        xp = xp + xpAwarded,
        reviewedCount = reviewedCount + 1,
        verifiedCount = verifiedCount + if (action == ModerationAction.VERIFIED) 1 else 0,
        rejectedCount = rejectedCount + if (action == ModerationAction.REJECTED) 1 else 0,
        resolvedCount = resolvedCount + if (action == ModerationAction.RESOLVED) 1 else 0
    )
}

/** Rangos del moderador según su XP. */
enum class ModeratorRank(val minXp: Int) {
    REVISOR(0),
    GUARDIAN(300),
    CURADOR(1000),
    EXPERTO(2500);

    val next: ModeratorRank? get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromXp(xp: Int): ModeratorRank = entries.last { xp >= it.minXp }
    }
}

/** Trofeos del moderador; cada uno se gana al llegar a [target]. */
enum class TrophyId(val target: Int) {
    FIRST_REVIEW(1),
    REVIEWS_10(10),
    REVIEWS_50(50),
    REVIEWS_100(100),
    FIRST_REJECTION(1),
    CRITICAL_EYE(25),
    QUALITY_SEAL(50),
    INTENSE_DAY(ModeratorXp.DAILY_BONUS_REVIEWS),
    CLOSER(10)
}

data class Trophy(val id: TrophyId, val progress: Int, val earnedAt: Long? = null) {
    val unlocked: Boolean get() = earnedAt != null || progress >= id.target
    val progressFraction: Float get() = (progress.toFloat() / id.target).coerceIn(0f, 1f)
}

/** Lo que se escribe en un solo lote al verificar, rechazar o resolver. */
data class ModerationDecision(
    val placeId: String,
    val placeTitle: String,
    val moderatorId: String,
    val action: ModerationAction,
    val reason: String?,
    val xpAwarded: Int,
    val newTrophies: Map<String, Long>
)
