package co.edu.uniquindio.apptours.core.util

// TODO(IT-11): pasar a recursos de texto con formato según el Locale.
object Formatters {

    fun formatDistance(distanceKm: Double): String =
        "${distanceKm.toString().replace('.', ',')} km"

    fun relevanceLabel(votes: Int): String = when {
        votes >= 40 -> "$votes votos · Alta"
        votes >= 15 -> "$votes votos · Media"
        else -> "$votes votos · En crecimiento"
    }
}
