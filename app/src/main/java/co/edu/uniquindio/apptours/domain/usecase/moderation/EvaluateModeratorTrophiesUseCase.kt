package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry
import co.edu.uniquindio.apptours.domain.model.ModeratorStats
import co.edu.uniquindio.apptours.domain.model.Trophy
import co.edu.uniquindio.apptours.domain.model.TrophyId
import java.util.Calendar
import javax.inject.Inject

/** Progreso real de cada trofeo a partir de los contadores y del historial del moderador. */
class EvaluateModeratorTrophiesUseCase @Inject constructor() {

    operator fun invoke(stats: ModeratorStats, log: List<ModerationLogEntry>): List<Trophy> {
        val bestDay = log.groupBy { dayOf(it.createdAt) }.maxOfOrNull { it.value.size } ?: 0
        // Ojo crítico cuenta rechazos con motivo (hoy todos lo tienen, pero se mide igual).
        val rejectionsWithReason = log.count { it.action == ModerationAction.REJECTED && !it.reason.isNullOrBlank() }
        return TrophyId.entries.map { id ->
            val progress = when (id) {
                TrophyId.FIRST_REVIEW, TrophyId.REVIEWS_10, TrophyId.REVIEWS_50, TrophyId.REVIEWS_100 -> stats.reviewedCount
                TrophyId.FIRST_REJECTION -> stats.rejectedCount
                TrophyId.CRITICAL_EYE -> rejectionsWithReason
                TrophyId.QUALITY_SEAL -> stats.verifiedCount
                TrophyId.INTENSE_DAY -> bestDay
                TrophyId.CLOSER -> stats.resolvedCount
            }
            Trophy(id = id, progress = progress.coerceAtMost(id.target), earnedAt = stats.trophies[id.name])
        }
    }

    /** Trofeos que se cumplen con [after] y aún no estaban guardados. */
    fun newlyEarned(after: ModeratorStats, logAfter: List<ModerationLogEntry>): List<TrophyId> =
        invoke(after, logAfter).filter { it.earnedAt == null && it.progress >= it.id.target }.map { it.id }

    private fun dayOf(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
