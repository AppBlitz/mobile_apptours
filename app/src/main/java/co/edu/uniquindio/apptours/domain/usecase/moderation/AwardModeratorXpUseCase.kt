package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModeratorXp
import javax.inject.Inject

/** XP de una acción (M-06): la de la tabla de [ModerationAction] y, si completa 10 revisiones hoy, +50. */
class AwardModeratorXpUseCase @Inject constructor() {
    operator fun invoke(action: ModerationAction, reviewsTodayBefore: Int): Int {
        val reachesDailyGoal = reviewsTodayBefore + 1 == ModeratorXp.DAILY_BONUS_REVIEWS
        return action.xp + if (reachesDailyGoal) ModeratorXp.DAILY_BONUS else 0
    }
}
