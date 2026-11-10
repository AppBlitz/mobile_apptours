package co.edu.uniquindio.apptours.features.user.placeform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.domain.usecase.place.CreatePlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.DeletePlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.UpdatePlaceUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
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
    private val getPlaceById: GetPlaceByIdUseCase,
    private val createPlace: CreatePlaceUseCase,
    private val updatePlace: UpdatePlaceUseCase,
    private val deletePlace: DeletePlaceUseCase
) : ViewModel() {

    private val placeId: String? = savedStateHandle["placeId"]

    private val _uiState = MutableStateFlow(PlaceFormUiState(isEditing = placeId != null, isLoading = placeId != null))
    val uiState: StateFlow<PlaceFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<PlaceFormEvent>(Channel.BUFFERED)
    val events: Flow<PlaceFormEvent> = _events.receiveAsFlow()

    init {
        loadPlace()
    }

    fun onRetry() = loadPlace()

    private fun loadPlace() {
        val id = placeId ?: return
        _uiState.update { it.copy(isLoading = true, loadErrorRes = null) }
        viewModelScope.launch {
            runCatching { getPlaceById(id).first() }
                .onSuccess { place ->
                    if (place == null) {
                        _uiState.update { it.copy(isLoading = false, loadErrorRes = R.string.error_place_not_found) }
                        return@onSuccess
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            originalStatus = place.status,
                            title = place.title,
                            category = place.category,
                            description = place.description,
                            priceRange = place.priceRange,
                            selectedDays = place.daysActive,
                            openTime = place.openTime,
                            closeTime = place.closeTime,
                            photos = place.imageUrls,
                            latitude = place.latitude,
                            longitude = place.longitude,
                            locationPicked = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, loadErrorRes = error.toMessageRes()) }
                }
        }
    }

    fun onTitleChange(value: String) = edit(FormField.TITLE) { it.copy(title = value) }

    fun onCategorySelected(value: PlaceCategory) = edit(FormField.CATEGORY) { it.copy(category = value) }

    fun onDescriptionChange(value: String) = edit(FormField.DESCRIPTION) {
        it.copy(description = value.take(PlaceForms.MAX_DESCRIPTION_LENGTH))
    }

    fun onPriceRangeSelected(value: PriceRange) = edit(FormField.PRICE_RANGE) { it.copy(priceRange = value) }

    fun onToggleDay(day: String) = edit(FormField.DAYS) {
        it.copy(selectedDays = if (day in it.selectedDays) it.selectedDays - day else it.selectedDays + day)
    }

    fun onOpenTimeChange(value: String) = edit(FormField.OPEN_TIME) { it.copy(openTime = value) }

    fun onCloseTimeChange(value: String) = edit(FormField.CLOSE_TIME) { it.copy(closeTime = value) }

    /** Fotos elegidas en el selector del sistema; nunca se pasa del máximo ni se repiten. */
    fun onPhotosPicked(uris: List<String>) {
        if (uris.isEmpty()) {
            _uiState.update { it.touch(FormField.PHOTOS) }
            return
        }
        edit(FormField.PHOTOS) { it.copy(photos = (it.photos + uris).distinct().take(PlaceForms.MAX_PHOTOS)) }
    }

    fun onRemovePhoto(photo: String) = edit(FormField.PHOTOS) { it.copy(photos = it.photos - photo) }

    fun onLocationChange(lat: Double, lng: Double) = edit(FormField.LOCATION) {
        it.copy(latitude = lat, longitude = lng, locationPicked = true)
    }

    fun onFieldFocusLost(field: FormField) {
        _uiState.update { it.touch(field) }
    }

    fun onSubmit() {
        val s = _uiState.value
        if (s.isSaving) return
        val errors = PlaceForms.place(s.draft)
        if (errors.isNotEmpty()) {
            showFieldErrors(errors)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorRes = null) }
            val onProgress: (Float) -> Unit = { p -> _uiState.update { it.copy(uploadProgress = p) } }
            val result = if (placeId == null) {
                createPlace(s.draft, onProgress).map { }
            } else {
                updatePlace(placeId, s.draft, onProgress)
            }
            _uiState.update { it.copy(isSaving = false, uploadProgress = null) }
            result
                .onSuccess { _events.send(PlaceFormEvent.Saved) }
                .onFailure { error ->
                    val fieldErrors = error.fieldErrors
                    if (fieldErrors.isNotEmpty()) {
                        showFieldErrors(fieldErrors)
                    } else {
                        _uiState.update { it.copy(errorRes = error.toMessageRes()) }
                    }
                }
        }
    }

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }

    fun onDismissDelete() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun onConfirmDelete() {
        val id = placeId ?: return
        _uiState.update { it.copy(showDeleteDialog = false, isSaving = true) }
        viewModelScope.launch {
            val result = deletePlace(id)
            _uiState.update { it.copy(isSaving = false) }
            result
                .onSuccess { _events.send(PlaceFormEvent.Deleted) }
                .onFailure { error -> _uiState.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }

    private fun edit(field: FormField, transform: (PlaceFormUiState) -> PlaceFormUiState) {
        _uiState.update { state ->
            val edited = transform(state).copy(errorRes = null).touch(field)
            // La hora de cierre se compara con la de apertura: si ya se tocó, se revalida junto con ella.
            if (field == FormField.OPEN_TIME && FormField.CLOSE_TIME in edited.form.touchedFields) {
                edited.touch(FormField.CLOSE_TIME)
            } else {
                edited
            }
        }
    }

    private fun showFieldErrors(errors: Map<FormField, FieldError>) {
        _uiState.update { it.copy(form = it.form.touchAll(PlaceForms.place(it.draft) + errors)) }
        PlaceForms.PLACE_ORDER.firstOrNull { it in errors }?.let {
            _events.trySend(PlaceFormEvent.ScrollToField(it))
        }
    }

    private fun PlaceFormUiState.touch(field: FormField) =
        copy(form = form.touch(field, PlaceForms.place(draft)))
}
