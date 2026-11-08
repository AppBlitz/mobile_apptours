package co.edu.uniquindio.apptours.core.common

/** Campos de formulario que pueden fallar en la validación de un caso de uso. */
enum class FormField {
    FULL_NAME,
    EMAIL,
    PASSWORD,
    PASSWORD_CONFIRMATION,
    TERMS,
    TITLE,
    CATEGORY,
    DESCRIPTION,
    PRICE_RANGE,
    DAYS,
    OPEN_TIME,
    CLOSE_TIME,
    PHOTOS,
    LOCATION,
    COMMENT,
    RATING,
    REJECTION_REASON
}

/** Errores de negocio. La UI los traduce a recursos de texto; nunca se muestra `message`. */
sealed class AppException : Exception() {
    data class InvalidFields(val errors: Map<FormField, FieldError>) : AppException() {
        /** Para formularios que todavía no detallan el motivo (se migran en IT-4/IT-5). */
        constructor(fields: Set<FormField>) : this(fields.associateWith { FieldError.Required })

        val fields: Set<FormField> get() = errors.keys
    }
    data object InvalidCredentials : AppException()
    data object EmailAlreadyInUse : AppException()
    data object WeakPassword : AppException()
    data object RecentLoginRequired : AppException()
    data object TooManyAttempts : AppException()
    data object AccountDisabled : AppException()
    data object NotAuthenticated : AppException()
    data object NotAllowed : AppException()
    data object PlaceNotFound : AppException()
    data object UserNotFound : AppException()
    data object Network : AppException()
}
