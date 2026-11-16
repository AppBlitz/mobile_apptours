package co.edu.uniquindio.apptours.domain.model

/** Insignias por logros (U-20). Cada una se gana al llegar a [target] con datos reales. */
enum class BadgeId(val target: Int, val category: PlaceCategory? = null) {
    FIRST_PLACE(1),
    FIRST_COMMENT(1),
    COMMENTATOR(10),
    TEN_VERIFIED(10),
    EXPLORER(UserLevel.EXPLORADOR.minPoints),
    ADVENTURER(UserLevel.AVENTURERO.minPoints),
    AMBASSADOR(UserLevel.EMBAJADOR_LOCAL.minPoints),
    FEATURED_OF_MONTH(1),
    TRAVELER(10),
    GASTRONOMY_LOVER(3, PlaceCategory.GASTRONOMIA),
    CULTURE_LOVER(3, PlaceCategory.CULTURA),
    NATURE_LOVER(3, PlaceCategory.NATURALEZA),
    ENTERTAINMENT_LOVER(3, PlaceCategory.ENTRETENIMIENTO),
    HISTORY_LOVER(3, PlaceCategory.HISTORIA)
}

/** Estado de una insignia para un usuario: progreso calculado y fecha real si ya la ganó. */
data class Badge(
    val id: BadgeId,
    val progress: Int,
    val earnedAt: Long? = null
) {
    val target: Int get() = id.target
    val unlocked: Boolean get() = earnedAt != null || progress >= target
    val progressFraction: Float get() = (progress.toFloat() / target).coerceIn(0f, 1f)
}
