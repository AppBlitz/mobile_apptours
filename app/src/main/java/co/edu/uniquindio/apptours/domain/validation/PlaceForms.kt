package co.edu.uniquindio.apptours.domain.validation

import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.PlaceDraft

/**
 * Reglas del formulario de publicación, del comentario y de "Comparte tu experiencia".
 * Las usan los ViewModels (validación en vivo) y los casos de uso (validación definitiva).
 */
object PlaceForms {

    const val MIN_TITLE_LENGTH = 3
    const val MAX_TITLE_LENGTH = 80
    const val MIN_DESCRIPTION_LENGTH = 20
    const val MAX_DESCRIPTION_LENGTH = 500
    const val MAX_PHOTOS = 6
    const val MAX_COMMENT_LENGTH = 500
    const val MIN_RATING = 1
    const val MAX_RATING = 5

    /** Orden visual del formulario: el primero inválido es al que se hace scroll. */
    val PLACE_ORDER = listOf(
        FormField.TITLE,
        FormField.CATEGORY,
        FormField.DESCRIPTION,
        FormField.PRICE_RANGE,
        FormField.DAYS,
        FormField.OPEN_TIME,
        FormField.CLOSE_TIME,
        FormField.PHOTOS,
        FormField.LOCATION
    )

    fun place(draft: PlaceDraft) = errorsOf(
        FormField.TITLE to Validators.text(draft.title, MIN_TITLE_LENGTH, MAX_TITLE_LENGTH),
        FormField.CATEGORY to selected(draft.category),
        FormField.DESCRIPTION to Validators.text(draft.description, MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH),
        FormField.PRICE_RANGE to selected(draft.priceRange),
        FormField.DAYS to if (draft.daysActive.isEmpty()) FieldError.Required else null,
        FormField.OPEN_TIME to Validators.time(draft.openTime),
        FormField.CLOSE_TIME to Validators.closeTime(draft.openTime, draft.closeTime),
        FormField.PHOTOS to if (draft.photosCount < 1) FieldError.Required else null,
        FormField.LOCATION to if (draft.latitude == null || draft.longitude == null) FieldError.Required else null
    )

    fun comment(text: String) = errorsOf(
        FormField.COMMENT to Validators.text(text, min = 1, max = MAX_COMMENT_LENGTH)
    )

    /** La calificación es obligatoria; el texto es opcional pero no puede pasar del máximo. */
    fun experience(rating: Int?, text: String) = errorsOf(
        FormField.RATING to if (rating == null || rating !in MIN_RATING..MAX_RATING) FieldError.Required else null,
        FormField.COMMENT to if (text.trim().length > MAX_COMMENT_LENGTH) FieldError.TooLong(MAX_COMMENT_LENGTH) else null
    )

    private fun selected(value: Any?): FieldError? = if (value == null) FieldError.Required else null

    private fun errorsOf(vararg checks: Pair<FormField, FieldError?>): Map<FormField, FieldError> =
        checks.mapNotNull { (field, error) -> error?.let { field to it } }.toMap()
}
