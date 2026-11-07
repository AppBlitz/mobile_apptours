package co.edu.uniquindio.apptours.features.user.placeform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.domain.usecase.place.CreatePlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.DeletePlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.UpdatePlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Sirve para las rutas CreatePlace (sin placeId) y EditPlace(placeId). */
@HiltViewModel
class PlaceFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaceById: GetPlaceByIdUseCase,
    private val createPlace: CreatePlaceUseCase,
    private val updatePlace: UpdatePlaceUseCase,
    private val deletePlace: DeletePlaceUseCase
) : ViewModel() {

    private val placeId: String? = savedStateHandle["placeId"]

    private val _uiState = MutableStateFlow(PlaceFormUiState(isEditing = placeId != null))
    val uiState: StateFlow<PlaceFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<PlaceFormEvent>(Channel.BUFFERED)
    val events: Flow<PlaceFormEvent> = _events.receiveAsFlow()

    init {
        if (placeId != null) {
            viewModelScope.launch {
                getPlaceById(placeId).first()?.let { place ->
                    _uiState.update {
                        it.copy(
                            title = place.title,
                            category = place.category,
                            description = place.description,
                            priceRange = place.priceRange,
                            selectedDays = place.daysActive,
                            openTime = place.openTime,
                            closeTime = place.closeTime,
                            photosCount = place.photosCount,
                            latitude = place.latitude,
                            longitude = place.longitude
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) = edit { it.copy(title = value) }

    fun onCategorySelected(value: PlaceCategory) = edit { it.copy(category = value) }

    fun onDescriptionChange(value: String) = edit { it.copy(description = value) }

    fun onPriceRangeSelected(value: PriceRange) = edit { it.copy(priceRange = value) }

    fun onToggleDay(day: String) = edit {
        it.copy(selectedDays = if (day in it.selectedDays) it.selectedDays - day else it.selectedDays + day)
    }

    fun onOpenTimeChange(value: String) = edit { it.copy(openTime = value) }

    fun onCloseTimeChange(value: String) = edit { it.copy(closeTime = value) }

    fun onAddPhoto() = edit { it.copy(photosCount = (it.photosCount + 1).coerceAtMost(MAX_PHOTOS)) }

    fun onRemovePhoto() = edit { it.copy(photosCount = (it.photosCount - 1).coerceAtLeast(1)) }

    fun onLocationChange(lat: Double, lng: Double) = edit { it.copy(latitude = lat, longitude = lng) }

    fun onSubmit() {
        val s = _uiState.value
        if (s.isSaving) return
        val draft = PlaceDraft(
            title = s.title,
            category = s.category,
            description = s.description,
            priceRange = s.priceRange,
            daysActive = s.selectedDays,
            openTime = s.openTime,
            closeTime = s.closeTime,
            latitude = s.latitude,
            longitude = s.longitude,
            photosCount = s.photosCount
        )
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = if (placeId == null) createPlace(draft).map { } else updatePlace(placeId, draft)
            result
                .onSuccess { _events.send(PlaceFormEvent.Saved) }
                .onFailure { error -> _uiState.update { it.copy(errorRes = error.toMessageRes()) } }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun onDelete() {
        val id = placeId ?: return
        viewModelScope.launch {
            deletePlace(id)
                .onSuccess { _events.send(PlaceFormEvent.Deleted) }
                .onFailure { error -> _uiState.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }

    private fun edit(transform: (PlaceFormUiState) -> PlaceFormUiState) {
        _uiState.update { transform(it).copy(errorRes = null) }
    }

    private companion object {
        const val MAX_PHOTOS = 6
    }
}
