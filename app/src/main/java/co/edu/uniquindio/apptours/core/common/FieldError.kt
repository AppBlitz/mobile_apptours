package co.edu.uniquindio.apptours.core.common

/** Motivo por el que un campo es inválido; la UI lo convierte en el mensaje que va debajo del campo. */
sealed interface FieldError {
    data object Required : FieldError
    data object InvalidEmail : FieldError
    data class TooShort(val missing: Int, val min: Int) : FieldError
    data class TooLong(val max: Int) : FieldError
    data object Mismatch : FieldError
    data object InvalidTime : FieldError
    data object CloseBeforeOpen : FieldError
}
