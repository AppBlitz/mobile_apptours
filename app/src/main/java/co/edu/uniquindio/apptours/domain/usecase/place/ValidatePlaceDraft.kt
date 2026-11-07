package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.domain.model.PlaceDraft

internal const val MAX_PHOTOS = 6
internal const val MAX_DESCRIPTION_LENGTH = 500

/** Validación compartida por crear y editar publicación. */
internal fun PlaceDraft.validate(): AppException.InvalidFields? {
    val invalidFields = buildSet {
        if (title.isBlank()) add(FormField.TITLE)
        if (description.isBlank() || description.length > MAX_DESCRIPTION_LENGTH) {
            add(FormField.DESCRIPTION)
        }
    }
    return if (invalidFields.isEmpty()) null else AppException.InvalidFields(invalidFields)
}
