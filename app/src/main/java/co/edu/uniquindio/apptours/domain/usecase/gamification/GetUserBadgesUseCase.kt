package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.Badge
import javax.inject.Inject

/**
 * Insignias del perfil. Se movió tal cual desde InMemoryDataSource para no cambiar la UI en IT-1.
 * TODO(IT-6): reemplazar por EvaluateBadgesUseCase con condiciones, fechas y progresos reales.
 */
class GetUserBadgesUseCase @Inject constructor() {

    operator fun invoke(points: Int, verifiedCount: Int): List<Badge> = listOf(
        Badge(
            id = "badge_1",
            title = "Primera publicación",
            description = "Compartiste tu primer punto de interés con la comunidad.",
            unlocked = true,
            obtainedDate = "Obtenida el 12 de septiembre"
        ),
        Badge(
            id = "badge_2",
            title = "Explorador",
            description = "Superaste los 300 puntos de reputación ayudando a viajeros.",
            unlocked = points >= 300,
            obtainedDate = "Obtenida el 28 de septiembre"
        ),
        Badge(
            id = "badge_3",
            title = "Comentarista",
            description = "Aportaste experiencias útiles en publicaciones de otros viajeros.",
            unlocked = true,
            obtainedDate = "Obtenida el 04 de octubre"
        ),
        Badge(
            id = "badge_4",
            title = "10 verificadas",
            description = "Consigue que los moderadores verifiquen 10 de tus publicaciones.",
            unlocked = verifiedCount >= 10,
            progressText = "$verifiedCount/10",
            progressFraction = (verifiedCount / 10f).coerceIn(0f, 1f)
        ),
        Badge(
            id = "badge_5",
            title = "Destacada del mes",
            description = "Logra la publicación con más votos «Me interesa» del mes.",
            unlocked = false,
            progressText = "En curso",
            progressFraction = 0.65f
        ),
        Badge(
            id = "badge_6",
            title = "Aventurero",
            description = "Alcanza los 1000 puntos de reputación en coLab tour.",
            unlocked = points >= 1000,
            progressText = "$points/1000",
            progressFraction = (points / 1000f).coerceIn(0f, 1f)
        )
    )
}
