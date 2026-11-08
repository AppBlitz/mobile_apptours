package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry
import co.edu.uniquindio.apptours.domain.model.ModeratorStats
import co.edu.uniquindio.apptours.domain.model.Trophy
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.ModerationRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import javax.inject.Inject

/** Todo lo que muestra el perfil del moderador (M-06), con datos reales y en tiempo real. */
data class ModeratorProfile(
    val user: User?,
    val stats: ModeratorStats,
    val rank: CalculateModeratorRankUseCase.RankProgress,
    val trophies: List<Trophy>,
    val reviewsToday: Int,
    val reviewsThisWeek: Int,
    /** Últimas 10 acciones, de la más reciente a la más antigua. */
    val recentActions: List<ModerationLogEntry>
)

class GetModeratorProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val moderationRepository: ModerationRepository,
    private val calculateRank: CalculateModeratorRankUseCase,
    private val evaluateTrophies: EvaluateModeratorTrophiesUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<ModeratorProfile?> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(null)
            combine(
                userRepository.observeUser(uid),
                moderationRepository.observeStats(uid),
                moderationRepository.observeLog(uid)
            ) { user, stats, log ->
                val sorted = log.sortedByDescending { it.createdAt }
                ModeratorProfile(
                    user = user,
                    stats = stats,
                    rank = calculateRank(stats.xp),
                    trophies = evaluateTrophies(stats, log),
                    reviewsToday = sorted.count { it.createdAt >= startOf(Calendar.DAY_OF_MONTH) },
                    reviewsThisWeek = sorted.count { it.createdAt >= startOf(Calendar.WEEK_OF_YEAR) },
                    recentActions = sorted.take(RECENT_ACTIONS)
                )
            }
        }

    /** Inicio del día o de la semana actual (según el primer día de la semana del dispositivo). */
    private fun startOf(field: Int): Long = Calendar.getInstance().apply {
        if (field == Calendar.WEEK_OF_YEAR) set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private companion object {
        const val RECENT_ACTIONS = 10
    }
}
