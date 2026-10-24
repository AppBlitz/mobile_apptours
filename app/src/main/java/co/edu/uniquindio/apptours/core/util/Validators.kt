package co.edu.uniquindio.apptours.core.util

import android.util.Patterns
import co.edu.uniquindio.apptours.domain.model.UserLevel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object Validators {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun calculateLevel(points: Int): UserLevel {
        return when {
            points >= 1500 -> UserLevel.EMBAJADOR_LOCAL
            points >= 1000 -> UserLevel.AVENTURERO
            points >= 300 -> UserLevel.EXPLORADOR
            else -> UserLevel.TURISTA
        }
    }

    fun pointsToNextLevel(points: Int): Pair<Int, String> {
        return when {
            points < 300 -> Pair(300 - points, UserLevel.EXPLORADOR.displayName)
            points < 1000 -> Pair(1000 - points, UserLevel.AVENTURERO.displayName)
            points < 1500 -> Pair(1500 - points, UserLevel.EMBAJADOR_LOCAL.displayName)
            else -> Pair(0, "Nivel Máximo")
        }
    }

    fun calculateDistanceKm(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val raw = earthRadius * c
        return (raw * 10.0).roundToInt() / 10.0
    }

    fun formatDistance(distanceKm: Double): String {
        return "${distanceKm.toString().replace('.', ',')} km"
    }

    fun relevanceLabel(votes: Int): String {
        return when {
            votes >= 40 -> "$votes votos · Alta"
            votes >= 15 -> "$votes votos · Media"
            else -> "$votes votos · En crecimiento"
        }
    }
}
