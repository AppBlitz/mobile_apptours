package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModerationDecision
import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.TrophyId
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.ModerationRepository
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

/**
 * Guarda una decisión de moderación (M-02, M-03, M-04) junto con el historial, la XP y los trofeos
 * del moderador (M-06), todo en un solo lote. Después avisa de cada trofeo nuevo.
 */
class RecordModerationDecisionUseCase @Inject constructor(
    private val moderationRepository: ModerationRepository,
    private val awardModeratorXp: AwardModeratorXpUseCase,
    private val evaluateTrophies: EvaluateModeratorTrophiesUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(
        moderator: User,
        place: Place,
        action: ModerationAction,
        reason: String? = null
    ): Result<Unit> {
        val stats = runCatching { moderationRepository.observeStats(moderator.id).first() }
            .getOrElse { return Result.failure(it) }
        val log = runCatching { moderationRepository.observeLog(moderator.id).first() }
            .getOrElse { return Result.failure(it) }

        val startOfToday = startOfToday()
        val xp = awardModeratorXp(action, reviewsTodayBefore = log.count { it.createdAt >= startOfToday })
        val now = System.currentTimeMillis()
        val statsAfter = stats.after(action, xp)
        val logAfter = log + ModerationLogEntry(
            id = "",
            moderatorId = moderator.id,
            placeId = place.id,
            placeTitle = place.title,
            action = action,
            reason = reason,
            xpAwarded = xp,
            createdAt = now
        )
        val newTrophies = evaluateTrophies.newlyEarned(statsAfter, logAfter)

        return moderationRepository.applyDecision(
            ModerationDecision(
                placeId = place.id,
                placeTitle = place.title,
                moderatorId = moderator.id,
                action = action,
                reason = reason,
                xpAwarded = xp,
                newTrophies = newTrophies.associate { it.name to now }
            )
        ).onSuccess {
            newTrophies.forEach { trophy ->
                sendNotification(
                    userId = moderator.id,
                    type = NotificationType.BADGE,
                    title = "¡Nuevo trofeo: ${trophy.displayName()}!",
                    body = "Lo encontrarás en tu perfil de moderador."
                )
            }
        }
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    // TODO(IT-11): el texto de la notificación se armará en la UI con recursos según el idioma.
    private fun TrophyId.displayName(): String = when (this) {
        TrophyId.FIRST_REVIEW -> "Primera revisión"
        TrophyId.REVIEWS_10 -> "10 revisiones"
        TrophyId.REVIEWS_50 -> "50 revisiones"
        TrophyId.REVIEWS_100 -> "100 revisiones"
        TrophyId.FIRST_REJECTION -> "Primer rechazo"
        TrophyId.CRITICAL_EYE -> "Ojo crítico"
        TrophyId.QUALITY_SEAL -> "Sello de calidad"
        TrophyId.INTENSE_DAY -> "Jornada intensa"
        TrophyId.CLOSER -> "Cerrador"
    }
}
