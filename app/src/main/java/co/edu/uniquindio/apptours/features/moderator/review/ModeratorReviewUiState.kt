package co.edu.uniquindio.apptours.features.moderator.review

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

/** Lista de verificación (T-06): lo que el moderador comprueba antes de aprobar. */
enum class ChecklistItem(@StringRes val labelRes: Int) {
    EXISTS(R.string.review_check_exists),
    LOCATION(R.string.review_check_location),
    SCHEDULE_PRICE(R.string.review_check_schedule_price),
    PHOTOS(R.string.review_check_photos)
}

data class ModeratorReviewUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val comments: List<Comment> = emptyList(),
    /** Empieza vacía: el moderador marca cada punto que comprobó. */
    val checkedItems: Set<ChecklistItem> = emptySet(),
    val showRejectSheet: Boolean = false,
    val rejectionReason: String = "",
    val rejectForm: FormState = FormState(),
    @StringRes val errorRes: Int? = null,
    @StringRes val loadErrorRes: Int? = null,
    val isProcessing: Boolean = false
) {
    /** Solo se puede aprobar con todos los puntos de la lista comprobados. */
    val canVerify: Boolean
        get() = checkedItems.size == ChecklistItem.entries.size && !isProcessing

    val status: PlaceStatus? get() = place?.status
}

sealed interface ModeratorReviewEvent {
    data object Done : ModeratorReviewEvent
    data class ScrollToField(val field: FormField) : ModeratorReviewEvent
}
