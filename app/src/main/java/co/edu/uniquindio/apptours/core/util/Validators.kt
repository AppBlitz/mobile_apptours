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

    // ---------- Reglas de contenido (integridad de los datos) ----------

    const val MIN_NAME_LENGTH = 3
    const val MAX_NAME_LENGTH = 60
    const val MAX_EMAIL_LENGTH = 100
    const val MAX_PASSWORD_LENGTH = 64

    /** Cuántas veces seguidas puede aparecer el mismo carácter ("aaaaa" o "!!!!!" ya es relleno). */
    const val MAX_REPEATED_CHARS = 4

    /** Letras de cualquier idioma (tildes, ñ, ü…) separadas por un espacio, guion o apóstrofo. */
    private val NAME_REGEX = Regex("""^\p{L}+(?:[ '’\-]\p{L}+)*$""")

    /** Título: letras, números, espacios y puntuación básica (sin @, $, *, <, >, emojis…). */
    private val TITLE_REGEX = Regex("""^[\p{L}\p{N} .,;:'’"&()\-#!¡?¿/+%]+$""")

    /** Etiquetas HTML y caracteres de control (salvo saltos de línea y tabulaciones). */
    private val FORBIDDEN_REGEX = Regex("""[<>\p{Cc}&&[^\n\r\t]]""")

    private val LETTER_REGEX = Regex("""\p{L}""")
    private val LETTER_OR_DIGIT_REGEX = Regex("""[\p{L}\p{N}]""")
    // Los números y espacios no cuentan: "100000" o varios espacios seguidos no son relleno.
    private val REPEATED_REGEX = Regex("""([^\p{N}\s])\1{$MAX_REPEATED_CHARS,}""")
    private val SPACES_REGEX = Regex("""\s+""")

    /** Nombre de persona: solo letras (con tildes), un espacio entre palabras, guiones y apóstrofos. */
    fun personName(value: String): FieldError? {
        val name = value.trim()
        return when {
            name.isEmpty() -> FieldError.Required
            !NAME_REGEX.matches(name) -> FieldError.InvalidName
            name.length < MIN_NAME_LENGTH -> FieldError.TooShort(MIN_NAME_LENGTH - name.length, MIN_NAME_LENGTH)
            name.length > MAX_NAME_LENGTH -> FieldError.TooLong(MAX_NAME_LENGTH)
            else -> null
        }
    }

    /** Correo con formato válido y de largo razonable. */
    fun emailAddress(value: String): FieldError? =
        email(value) ?: if (value.trim().length > MAX_EMAIL_LENGTH) FieldError.TooLong(MAX_EMAIL_LENGTH) else null

    /** Contraseña nueva segura: mínimo de caracteres, letras y números, y sin espacios en los extremos. */
    fun strongPassword(value: String): FieldError? = newPassword(value) ?: when {
        value.length > MAX_PASSWORD_LENGTH -> FieldError.TooLong(MAX_PASSWORD_LENGTH)
        value != value.trim() -> FieldError.PasswordEdgeSpaces
        value.none { it.isLetter() } || value.none { it.isDigit() } -> FieldError.PasswordNeedsLetterAndDigit
        else -> null
    }

    /** Título de una publicación: largo, caracteres permitidos y al menos una letra. */
    fun title(value: String, min: Int, max: Int): FieldError? {
        text(value, min, max)?.let { return it }
        val title = value.trim()
        return when {
            !TITLE_REGEX.matches(title) -> FieldError.InvalidTitleCharacters
            !LETTER_REGEX.containsMatchIn(title) -> FieldError.NoLetters
            REPEATED_REGEX.containsMatchIn(title) -> FieldError.RepeatedCharacters(MAX_REPEATED_CHARS)
            else -> null
        }
    }

    /**
     * Texto libre (descripción, comentario, motivo): largo, sin etiquetas ni caracteres de control,
     * con palabras de verdad y sin relleno de un mismo carácter. [required] = false permite dejarlo vacío.
     */
    fun freeText(value: String, min: Int, max: Int, required: Boolean = true): FieldError? {
        val text = value.trim()
        if (text.isEmpty()) return if (required) FieldError.Required else null
        text(value, min, max)?.let { return it }
        return content(text, needsLetters = true)
    }

    /** Comentario corto: además de lo de [freeText], basta con que tenga letras o números (p. ej. "10/10"). */
    fun comment(value: String, max: Int, required: Boolean = true): FieldError? {
        val text = value.trim()
        if (text.isEmpty()) return if (required) FieldError.Required else null
        if (text.length > max) return FieldError.TooLong(max)
        return content(text, needsLetters = false)
    }

    private fun content(text: String, needsLetters: Boolean): FieldError? = when {
        FORBIDDEN_REGEX.containsMatchIn(text) -> FieldError.ForbiddenCharacters
        needsLetters && !LETTER_REGEX.containsMatchIn(text) -> FieldError.NoLetters
        !needsLetters && !LETTER_OR_DIGIT_REGEX.containsMatchIn(text) -> FieldError.NoLetters
        REPEATED_REGEX.containsMatchIn(text) -> FieldError.RepeatedCharacters(MAX_REPEATED_CHARS)
        else -> null
    }

    /** Quita espacios en los extremos y deja uno solo entre palabras (para nombres y títulos). */
    fun singleSpaced(value: String): String = value.trim().replace(SPACES_REGEX, " ")

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
