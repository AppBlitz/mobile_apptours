package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.BadgeId
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
                        title = "¡Nueva insignia: ${badge.displayName()}!",
                        body = "La encontrarás en tu perfil, con la fecha en que la ganaste."
                    )
                }
            }
            newlyEarned
        }
    }

    // TODO(IT-11): el texto de la notificación se armará en la UI con recursos según el idioma.
    private fun BadgeId.displayName(): String = when (this) {
        BadgeId.FIRST_PLACE -> "Primera publicación"
        BadgeId.FIRST_COMMENT -> "Primer comentario"
        BadgeId.COMMENTATOR -> "Comentarista"
        BadgeId.TEN_VERIFIED -> "10 verificadas"
        BadgeId.EXPLORER -> "Explorador"
        BadgeId.ADVENTURER -> "Aventurero"
        BadgeId.AMBASSADOR -> "Embajador Local"
        BadgeId.FEATURED_OF_MONTH -> "Lugar más destacado del mes"
        BadgeId.TRAVELER -> "Viajero"
        BadgeId.GASTRONOMY_LOVER -> "Amante de la gastronomía"
        BadgeId.CULTURE_LOVER -> "Amante de la cultura"
        BadgeId.NATURE_LOVER -> "Amante de la naturaleza"
        BadgeId.ENTERTAINMENT_LOVER -> "Amante del entretenimiento"
        BadgeId.HISTORY_LOVER -> "Amante de la historia"
    }
}
