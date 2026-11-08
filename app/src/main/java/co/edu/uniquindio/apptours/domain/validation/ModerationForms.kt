package co.edu.uniquindio.apptours.domain.validation

import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators

/** Reglas del motivo de rechazo (M-03): las usan el ViewModel de revisión y RejectPlaceUseCase. */
object ModerationForms {

    /** Un motivo de una o dos palabras no le sirve al autor para corregir su publicación. */
    const val MIN_REASON_LENGTH = 15
    const val MAX_REASON_LENGTH = 300

    fun rejection(reason: String): Map<FormField, FieldError> =
        Validators.text(reason, MIN_REASON_LENGTH, MAX_REASON_LENGTH)
            ?.let { mapOf(FormField.REJECTION_REASON to it) }
            .orEmpty()
}
