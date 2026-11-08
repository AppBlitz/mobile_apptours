package co.edu.uniquindio.apptours.domain.model

/**
 * Contadores reales del panel: cuántas publicaciones hay en cada filtro y cuántas
 * se verificaron, rechazaron o resolvieron hoy (según `reviewedAt`).
 */
data class ModerationStats(
    val totals: Map<ModerationFilter, Int> = emptyMap(),
    val verifiedToday: Int = 0,
    val rejectedToday: Int = 0,
    val resolvedToday: Int = 0
) {
    val pendingCount: Int get() = countOf(ModerationFilter.PENDIENTES)

    fun countOf(filter: ModerationFilter): Int = totals[filter] ?: 0
}
