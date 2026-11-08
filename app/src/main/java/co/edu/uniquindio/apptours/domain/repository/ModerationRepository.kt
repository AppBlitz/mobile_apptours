package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.ModerationDecision
import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry
import co.edu.uniquindio.apptours.domain.model.ModeratorStats
import kotlinx.coroutines.flow.Flow

/** Perfil del moderador (M-06): sus contadores y su historial permanente. */
interface ModerationRepository {
    fun observeStats(moderatorId: String): Flow<ModeratorStats>
    fun observeLog(moderatorId: String): Flow<List<ModerationLogEntry>>

    /**
     * Aplica la decisión en un solo lote: nuevo estado de la publicación, entrada en el historial
     * y contadores / XP / trofeos del moderador. Si una parte falla, no se escribe nada.
     */
    suspend fun applyDecision(decision: ModerationDecision): Result<Unit>
}
