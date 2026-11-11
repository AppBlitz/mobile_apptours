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

    /** Nombre con números, símbolos o espacios dobles: solo se aceptan letras, espacios, guiones y apóstrofos. */
    data object InvalidName : FieldError

    /** La contraseña nueva debe combinar letras y números. */
    data object PasswordNeedsLetterAndDigit : FieldError

    /** La contraseña no puede empezar ni terminar con espacios. */
    data object PasswordEdgeSpaces : FieldError

    /** Título con símbolos que no son de puntuación básica (@, $, %, <, >, emojis…). */
    data object InvalidTitleCharacters : FieldError

    /** Texto con caracteres prohibidos (< > o de control) que podrían dañar cómo se muestra. */
    data object ForbiddenCharacters : FieldError

    /** El texto no tiene palabras: solo números, símbolos o emojis. */
    data object NoLetters : FieldError

    /** El mismo carácter repetido muchas veces seguidas (relleno como "aaaaaaa" o "!!!!!!!"). */
    data class RepeatedCharacters(val max: Int) : FieldError
}
