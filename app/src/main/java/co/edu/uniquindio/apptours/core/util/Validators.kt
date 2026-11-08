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
}
