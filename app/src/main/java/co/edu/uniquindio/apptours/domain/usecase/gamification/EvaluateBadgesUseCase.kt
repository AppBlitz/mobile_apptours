package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.BadgeId
import co.edu.uniquindio.apptours.domain.model.NotificationArgs
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.notification.SendNotificationUseCase
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Revisa las insignias del usuario en sesión (U-20) y guarda con fecha real las que acaba de ganar,
 * con una notificación de logro por cada una. Se llama después de cada acción propia (publicar,
 * comentar, visitar) y al abrir la app, para recoger las que dependen de otros (votos, verificaciones).
 * Solo el dueño escribe sus insignias, por eso no se evalúa desde la acción de otro usuario.
 */
class EvaluateBadgesUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository,
    private val userRepository: UserRepository,
    private val sendNotification: SendNotificationUseCase
) {
    suspend operator fun invoke(): Result<List<BadgeId>> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        return runCatching {
            val badges = BadgeCalculator.calculate(
                user = user,
                ownPlaces = placeRepository.observePlacesByAuthor(user.id).first(),
                visits = visitRepository.observeVisits(user.id).first(),
                publicPlaces = placeRepository.observePublicPlaces().first()
            )
            val newlyEarned = badges.filter { it.earnedAt == null && it.progress >= it.target }.map { it.id }
            if (newlyEarned.isNotEmpty()) {
                val now = System.currentTimeMillis()
                userRepository.addBadges(user.id, newlyEarned.associate { it.name to now }).getOrThrow()
                newlyEarned.forEach { badge ->
                    sendNotification(
                        userId = user.id,
                        type = NotificationType.BADGE,
                        args = mapOf(NotificationArgs.BADGE to badge.name)
                    )
                }
            }
            newlyEarned
        }
    }
}
