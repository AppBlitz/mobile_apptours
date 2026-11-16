package co.edu.uniquindio.apptours.core.common

/**
 * Estado de validación de un formulario (ROADMAP 4.3). Los errores se recalculan con cada cambio,
 * pero solo se muestran en los campos que el usuario ya tocó (escribió o dejó al perder el foco).
 */
data class FormState(
    val fieldErrors: Map<FormField, FieldError> = emptyMap(),
    val touchedFields: Set<FormField> = emptySet()
) {
    fun errorOf(field: FormField): FieldError? =
        if (field in touchedFields) fieldErrors[field] else null

    /** El usuario editó o dejó [field]: se marca como tocado con los errores recién calculados. */
    fun touch(field: FormField, errors: Map<FormField, FieldError>): FormState =
        FormState(errors, touchedFields + field)

    /** Al enviar: todos los campos inválidos pasan a mostrarse. */
    fun touchAll(errors: Map<FormField, FieldError>): FormState =
        FormState(errors, touchedFields + errors.keys)

    /** Primer campo inválido según el orden visual de la pantalla. */
    fun firstInvalid(order: List<FormField>): FormField? = order.firstOrNull { it in fieldErrors }
}
