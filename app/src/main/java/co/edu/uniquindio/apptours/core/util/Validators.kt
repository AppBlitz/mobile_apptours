package co.edu.uniquindio.apptours.core.util

import co.edu.uniquindio.apptours.core.common.FieldError

/** Validaciones en Kotlin puro (sin Android) para poder usarlas desde los casos de uso. */
object Validators {

    const val MIN_PASSWORD_LENGTH = 8

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun required(value: String): FieldError? = if (value.isBlank()) FieldError.Required else null

    fun email(value: String): FieldError? = when {
        value.isBlank() -> FieldError.Required
        !isValidEmail(value) -> FieldError.InvalidEmail
        else -> null
    }

    /** Contraseña nueva: dice cuántos caracteres faltan para el mínimo. */
    fun newPassword(value: String): FieldError? = when {
        value.isEmpty() -> FieldError.Required
        value.length < MIN_PASSWORD_LENGTH ->
            FieldError.TooShort(missing = MIN_PASSWORD_LENGTH - value.length, min = MIN_PASSWORD_LENGTH)
        else -> null
    }

    fun confirmation(password: String, confirmation: String): FieldError? = when {
        confirmation.isEmpty() -> FieldError.Required
        confirmation != password -> FieldError.Mismatch
        else -> null
    }

    fun accepted(value: Boolean): FieldError? = if (value) null else FieldError.Required

    /** Texto obligatorio con longitud mínima y máxima (cuenta sin espacios al inicio ni al final). */
    fun text(value: String, min: Int, max: Int): FieldError? {
        val length = value.trim().length
        return when {
            length == 0 -> FieldError.Required
            length < min -> FieldError.TooShort(missing = min - length, min = min)
            length > max -> FieldError.TooLong(max)
            else -> null
        }
    }

    fun time(value: String): FieldError? = when {
        value.isBlank() -> FieldError.Required
        minutesOfDay(value) == null -> FieldError.InvalidTime
        else -> null
    }

    /** La hora de cierre debe ser posterior a la de apertura (solo si ambas son válidas). */
    fun closeTime(open: String, close: String): FieldError? {
        time(close)?.let { return it }
        val openMinutes = minutesOfDay(open) ?: return null
        val closeMinutes = minutesOfDay(close) ?: return null
        return if (closeMinutes <= openMinutes) FieldError.CloseBeforeOpen else null
    }

    private val TIME_REGEX = Regex("""^(\d{1,2}):(\d{2})\s*(?:([ap])\.?\s*m\.?)?$""", RegexOption.IGNORE_CASE)

    /**
     * Minutos desde la medianoche para "7:00 a. m.", "7:30 pm" o "19:00"; null si el formato no es válido.
     * Con a. m. / p. m. la hora va de 1 a 12; sin ellos, de 0 a 23.
     */
    fun minutesOfDay(value: String): Int? {
        val match = TIME_REGEX.matchEntire(value.trim()) ?: return null
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        if (minute > 59) return null
        val meridiem = match.groupValues[3].lowercase()
        val hour24 = when (meridiem) {
            "" -> hour.takeIf { it in 0..23 }
            "a" -> hour.takeIf { it in 1..12 }?.let { if (it == 12) 0 else it }
            else -> hour.takeIf { it in 1..12 }?.let { if (it == 12) 12 else it + 12 }
        } ?: return null
        return hour24 * 60 + minute
    }
}
