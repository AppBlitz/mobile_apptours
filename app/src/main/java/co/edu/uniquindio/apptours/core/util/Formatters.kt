package co.edu.uniquindio.apptours.core.util

import java.text.DateFormat
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

/** Formatos que dependen del idioma: "1,2 km" en español, "1.2 km" en inglés. */
object Formatters {

    fun formatDistance(distanceKm: Double, locale: Locale = Locale.getDefault()): String {
        val number = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 1
        }
        return "${number.format(distanceKm)} km"
    }

    /**
     * Hora escrita por el autor ("7:00 a. m.", "19:00") mostrada en el formato del idioma
     * ("7:00 a. m." / "7:00 AM"). Si no se reconoce el formato se deja tal cual.
     */
    fun formatTime(value: String, locale: Locale = Locale.getDefault()): String {
        val minutes = Validators.minutesOfDay(value) ?: return value
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, minutes / 60)
            set(Calendar.MINUTE, minutes % 60)
        }
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(calendar.time)
    }
}
