package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.UserLevel
import javax.inject.Inject

/** Nivel según los puntos (T-07): Turista 0, Explorador 300, Aventurero 1000, Embajador Local 1500. */
class CalculateLevelUseCase @Inject constructor() {
    operator fun invoke(points: Int): UserLevel = UserLevel.fromPoints(points)

    /** Avance (0..1) desde el nivel actual hacia el siguiente; 1 si ya está en el máximo. */
    fun progressToNext(points: Int): Float {
        val current = invoke(points)
        val next = current.next ?: return 1f
        return ((points - current.minPoints).toFloat() / (next.minPoints - current.minPoints)).coerceIn(0f, 1f)
    }
}
