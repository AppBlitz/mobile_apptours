package co.edu.uniquindio.apptours.features.user.placedetail

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place

data class PlaceDetailUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val comments: List<Comment> = emptyList(),
    val currentUserId: String? = null,
    val userLatitude: Double = DefaultLocation.LATITUDE,
    val userLongitude: Double = DefaultLocation.LONGITUDE,
    val isInterested: Boolean = false,
    val isVisited: Boolean = false,
    val commentText: String = "",
    val commentForm: FormState = FormState(),
    val isSendingComment: Boolean = false,
    /** Diálogo "Comparte tu experiencia" abierto (null = cerrado). */
    val experience: VisitExperienceState? = null,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    @StringRes val errorRes: Int? = null,
    @StringRes val loadErrorRes: Int? = null
) {
    val isOwner: Boolean
        get() = place != null && place.authorId == currentUserId
}

/** Lo que el usuario va llenando en "Comparte tu experiencia": calificación obligatoria y texto opcional. */
data class VisitExperienceState(
    val text: String = "",
    val rating: Int? = null,
    val form: FormState = FormState(),
    val isSaving: Boolean = false
)

sealed interface PlaceDetailEvent {
    data object Deleted : PlaceDetailEvent
    data class ScrollToField(val field: FormField) : PlaceDetailEvent
}
