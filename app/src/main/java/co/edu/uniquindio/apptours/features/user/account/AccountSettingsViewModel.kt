package co.edu.uniquindio.apptours.features.user.account

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.usecase.auth.DeleteAccountUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountSettingsUiState(
    val isLoaded: Boolean = false,
    @StringRes val loadErrorRes: Int? = null,
    val isSaving: Boolean = false,
    val initials: String = "",
    val fullName: String = "",
    val email: String = "",
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val savedBanner: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    @StringRes val errorRes: Int? = null
)

sealed interface AccountSettingsEvent {
    data object AccountDeleted : AccountSettingsEvent
}

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val deleteAccount: DeleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountSettingsUiState())
    val uiState: StateFlow<AccountSettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<AccountSettingsEvent>(Channel.BUFFERED)
    val events: Flow<AccountSettingsEvent> = _events.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        loadUser()
    }

    fun onRetry() = loadUser()

    private fun loadUser() {
        loadJob?.cancel()
        _uiState.update { it.copy(loadErrorRes = null) }
        loadJob = viewModelScope.launch {
            getCurrentUser().filterNotNull()
                .catch { error -> _uiState.update { it.copy(loadErrorRes = error.toMessageRes()) } }
                .collect { user ->
                    _uiState.update { state ->
                        if (state.isLoaded) {
                            state.copy(initials = user.initials)
                        } else {
                            state.copy(
                                isLoaded = true,
                                initials = user.initials,
                                fullName = user.fullName,
                                email = user.email,
                                latitude = user.latitude,
                                longitude = user.longitude
                            )
                        }
                    }
                }
        }
    }

    fun onFullNameChange(value: String) =
        _uiState.update { it.copy(fullName = value, savedBanner = false, errorRes = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, savedBanner = false, errorRes = null) }

    /** Simula elegir otro punto en el mapa. TODO(IT-7): usar MapLocationPicker con Mapbox. */
    fun onChangeLocation() = _uiState.update {
        it.copy(
            latitude = ((it.latitude + 0.0015) * 10000).toInt() / 10000.0,
            longitude = ((it.longitude - 0.0012) * 10000).toInt() / 10000.0,
            savedBanner = false
        )
    }

    fun onSave() {
        val s = _uiState.value
        if (s.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            updateProfile(s.fullName, s.email, s.latitude, s.longitude)
                .onSuccess { _uiState.update { it.copy(isSaving = false, savedBanner = true) } }
                .onFailure { error -> _uiState.update { it.copy(isSaving = false, errorRes = error.toMessageRes()) } }
        }
    }

    fun onRequestDelete() = _uiState.update { it.copy(showDeleteConfirm = true) }

    fun onDismissDelete() = _uiState.update { it.copy(showDeleteConfirm = false) }

    fun onConfirmDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false, isSaving = true) }
        viewModelScope.launch {
            deleteAccount()
                .onSuccess { _events.send(AccountSettingsEvent.AccountDeleted) }
                .onFailure { error -> _uiState.update { it.copy(isSaving = false, errorRes = error.toMessageRes()) } }
        }
    }
}
