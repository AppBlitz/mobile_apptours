package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModeratorRank
import javax.inject.Inject

/** Rango y avance hacia el siguiente: Revisor 0, Guardián 300, Curador 1000, Moderador Experto 2500. */
class CalculateModeratorRankUseCase @Inject constructor() {

    data class RankProgress(val rank: ModeratorRank, val next: ModeratorRank?, val xpToNext: Int, val fraction: Float)

    operator fun invoke(xp: Int): RankProgress {
        val rank = ModeratorRank.fromXp(xp)
        val next = rank.next ?: return RankProgress(rank, null, 0, 1f)
        val fraction = (xp - rank.minXp).toFloat() / (next.minXp - rank.minXp)
        return RankProgress(rank, next, next.minXp - xp, fraction.coerceIn(0f, 1f))
    }
}
