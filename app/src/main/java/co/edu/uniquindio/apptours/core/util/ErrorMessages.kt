package co.edu.uniquindio.apptours.core.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState

/** Mensaje de error para un campo inválido del formulario. */
@StringRes
fun FormField.errorRes(): Int = when (this) {
    FormField.FULL_NAME -> R.string.error_full_name_required
    FormField.EMAIL -> R.string.error_invalid_email
    FormField.PASSWORD -> R.string.error_password_required
    FormField.PASSWORD_CONFIRMATION -> R.string.error_password_mismatch
    FormField.TERMS -> R.string.error_terms_required
    FormField.TITLE, FormField.DESCRIPTION -> R.string.error_place_required_fields
    FormField.COMMENT -> R.string.error_comment_required
    FormField.REJECTION_REASON -> R.string.error_reject_reason_required
}

/** Mensaje de error general para cualquier fallo devuelto por un caso de uso. */
@StringRes
fun Throwable.toMessageRes(): Int = when (this) {
    is AppException.InvalidFields -> fields.firstOrNull()?.errorRes() ?: R.string.error_unknown
    AppException.InvalidCredentials -> R.string.error_invalid_credentials
    AppException.EmailAlreadyInUse -> R.string.error_email_in_use
    AppException.WeakPassword -> R.string.error_weak_password
    AppException.RecentLoginRequired -> R.string.error_recent_login_required
    AppException.TooManyAttempts -> R.string.error_too_many_attempts
    AppException.AccountDisabled -> R.string.error_account_disabled
    AppException.NotAuthenticated -> R.string.error_not_authenticated
    AppException.NotAllowed -> R.string.error_not_allowed
    AppException.PlaceNotFound -> R.string.error_place_not_found
    AppException.UserNotFound -> R.string.error_user_not_found
    AppException.Network -> R.string.error_network
    else -> R.string.error_unknown
}

/** Campos inválidos de la excepción, o vacío si el error no es de validación. */
val Throwable.invalidFields: Set<FormField>
    get() = (this as? AppException.InvalidFields)?.fields.orEmpty()

/** Motivo de cada campo inválido, o vacío si el error no es de validación. */
val Throwable.fieldErrors: Map<FormField, FieldError>
    get() = (this as? AppException.InvalidFields)?.errors.orEmpty()

/** Mensaje del campo si está entre los inválidos, o null. */
@StringRes
fun Set<FormField>.errorFor(field: FormField, @StringRes message: Int = field.errorRes()): Int? =
    if (field in this) message else null

/** Mensaje que va justo debajo del campo: explica qué está mal y cómo corregirlo. */
@Composable
fun FieldError.message(field: FormField): String = when (this) {
    FieldError.Required -> stringResource(
        when (field) {
            FormField.EMAIL -> R.string.error_email_required
            FormField.PASSWORD_CONFIRMATION -> R.string.error_password_confirmation_required
            else -> field.errorRes()
        }
    )
    FieldError.InvalidEmail -> stringResource(R.string.error_invalid_email)
    is FieldError.TooShort -> pluralStringResource(R.plurals.error_password_too_short, missing, missing, min)
    FieldError.Mismatch -> stringResource(R.string.error_password_mismatch)
}

/** Mensaje visible del campo en el formulario, o null si no está tocado o es válido. */
@Composable
fun FormState.messageOf(field: FormField): String? = errorOf(field)?.message(field)
