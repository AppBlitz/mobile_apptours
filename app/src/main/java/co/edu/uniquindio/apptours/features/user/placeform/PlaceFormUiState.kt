package co.edu.uniquindio.apptours.features.user.placeform

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PriceRange

data class PlaceFormUiState(
    val isEditing: Boolean = false,
    val title: String = "",
    val category: PlaceCategory = PlaceCategory.GASTRONOMIA,
    val description: String = "",
    val priceRange: PriceRange = PriceRange.ECONOMICO,
    val selectedDays: List<String> = Place.DEFAULT_DAYS,
    val openTime: String = "7:00 a. m.",
    val closeTime: String = "7:00 p. m.",
    val photosCount: Int = 2,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    @StringRes val errorRes: Int? = null,
    val isSaving: Boolean = false
)

sealed interface PlaceFormEvent {
    data object Saved : PlaceFormEvent
    data object Deleted : PlaceFormEvent
}
