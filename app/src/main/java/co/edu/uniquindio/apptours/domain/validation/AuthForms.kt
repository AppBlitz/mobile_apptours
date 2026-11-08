package co.edu.uniquindio.apptours.domain.validation

import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators

/**
 * Reglas de los formularios de cuenta. Las usan los ViewModels (validación en vivo) y los casos
 * de uso (validación definitiva antes de llamar al servidor), así hay una sola fuente de reglas.
 */
object AuthForms {

    /** Orden visual de los campos: el primero inválido es al que se hace scroll. */
    val LOGIN_ORDER = listOf(FormField.EMAIL, FormField.PASSWORD)
    val REGISTER_ORDER = listOf(
        FormField.FULL_NAME,
        FormField.EMAIL,
        FormField.PASSWORD,
        FormField.PASSWORD_CONFIRMATION,
        FormField.TERMS
    )
    val PROFILE_ORDER = listOf(FormField.FULL_NAME, FormField.EMAIL)

    fun login(email: String, password: String) = errorsOf(
        FormField.EMAIL to Validators.email(email),
        FormField.PASSWORD to Validators.required(password)
    )

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmation: String,
        termsAccepted: Boolean
    ) = errorsOf(
        FormField.FULL_NAME to Validators.required(fullName),
        FormField.EMAIL to Validators.email(email),
        FormField.PASSWORD to Validators.newPassword(password),
        FormField.PASSWORD_CONFIRMATION to Validators.confirmation(password, confirmation),
        FormField.TERMS to Validators.accepted(termsAccepted)
    )

    fun passwordReset(email: String) = errorsOf(FormField.EMAIL to Validators.email(email))

    fun profile(fullName: String, email: String) = errorsOf(
        FormField.FULL_NAME to Validators.required(fullName),
        FormField.EMAIL to Validators.email(email)
    )

    private fun errorsOf(vararg checks: Pair<FormField, FieldError?>): Map<FormField, FieldError> =
        checks.mapNotNull { (field, error) -> error?.let { field to it } }.toMap()
}
