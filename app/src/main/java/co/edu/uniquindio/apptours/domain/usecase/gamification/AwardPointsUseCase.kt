package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.model.UserLevel
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

/**
 * Único punto donde se otorgan puntos (U-18), según la tabla de [PointAction].
 * Si la suma hace subir de nivel (T-07, U-19) el usuario recibe una notificación de logro.
 */
class AwardPointsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val calculateLevel: CalculateLevelUseCase,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(userId: String, action: PointAction): Result<Unit> =
        userRepository.addPoints(userId, action.points).map { change ->
            val before = calculateLevel(change.before)
            val after = calculateLevel(change.after)
            if (after.ordinal > before.ordinal) notifyLevelUp(userId, after)
        }

    private suspend fun notifyLevelUp(userId: String, level: UserLevel) {
        sendNotification(
            userId = userId,
            type = NotificationType.LEVEL_UP,
            title = "¡Subiste de nivel! Ahora eres ${level.displayName()}",
            body = "Llegaste a ${level.minPoints} puntos de reputación. Sigue compartiendo lugares y experiencias."
        )
    }

    // TODO(IT-11): el texto de la notificación se armará en la UI con recursos según el idioma.
    private fun UserLevel.displayName(): String = when (this) {
        UserLevel.TURISTA -> "Turista"
        UserLevel.EXPLORADOR -> "Explorador"
        UserLevel.AVENTURERO -> "Aventurero"
        UserLevel.EMBAJADOR_LOCAL -> "Embajador Local"
    }
}
