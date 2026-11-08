package co.edu.uniquindio.apptours.features.user.placeform

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PriceRange

data class PlaceFormUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    @StringRes val loadErrorRes: Int? = null,
    /** Estado que tenía la publicación al abrirla para editar (para avisar que vuelve a revisión). */
    val originalStatus: PlaceStatus? = null,
    val title: String = "",
    val category: PlaceCategory? = null,
    val description: String = "",
    val priceRange: PriceRange? = null,
    val selectedDays: List<String> = Place.DEFAULT_DAYS,
    val openTime: String = "7:00 a. m.",
    val closeTime: String = "7:00 p. m.",
    val photosCount: Int = 0,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val locationPicked: Boolean = false,
    val form: FormState = FormState(),
    @StringRes val errorRes: Int? = null,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false
) {
    val draft: PlaceDraft
        get() = PlaceDraft(
            title = title,
            category = category,
            description = description,
            priceRange = priceRange,
            daysActive = selectedDays,
            openTime = openTime,
            closeTime = closeTime,
            latitude = latitude.takeIf { locationPicked },
            longitude = longitude.takeIf { locationPicked },
            photosCount = photosCount
        )

    /** Editar algo ya revisado (verificado, rechazado o resuelto) lo devuelve a la cola de moderación. */
    val returnsToReview: Boolean
        get() = isEditing && originalStatus != null && originalStatus != PlaceStatus.PENDIENTE
}

sealed interface PlaceFormEvent {
    data object Saved : PlaceFormEvent
    data object Deleted : PlaceFormEvent
    data class ScrollToField(val field: FormField) : PlaceFormEvent
}
