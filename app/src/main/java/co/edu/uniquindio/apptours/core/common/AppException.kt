package co.edu.uniquindio.apptours.core.common

/** Campos de formulario que pueden fallar en la validación de un caso de uso. */
enum class FormField {
    FULL_NAME,
    EMAIL,
    PASSWORD,
    PASSWORD_CONFIRMATION,
    TERMS,
    TITLE,
    DESCRIPTION,
    COMMENT,
    REJECTION_REASON
}

/** Errores de negocio. La UI los traduce a recursos de texto; nunca se muestra `message`. */
sealed class AppException : Exception() {
    data class InvalidFields(val fields: Set<FormField>) : AppException()
    data object InvalidCredentials : AppException()
    data object EmailAlreadyInUse : AppException()
    data object NotAuthenticated : AppException()
    data object NotAllowed : AppException()
    data object PlaceNotFound : AppException()
    data object UserNotFound : AppException()
    data object Network : AppException()
}
