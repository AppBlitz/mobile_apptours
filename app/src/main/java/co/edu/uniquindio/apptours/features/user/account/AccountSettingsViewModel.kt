package co.edu.uniquindio.apptours.features.user.account

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.usecase.auth.DeleteAccountUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.UpdateProfilePhotoUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.UpdateProfileUseCase
import co.edu.uniquindio.apptours.domain.validation.AuthForms
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
    val photoUrl: String = "",
    /** Avance de 0 a 1 mientras se carga la foto de perfil; null si no se está cargando. */
    val photoUploadProgress: Float? = null,
    val photoSavedBanner: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    /** Las cuentas de Google no cambian el correo desde la app. */
    val canChangeEmail: Boolean = true,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val form: FormState = FormState(),
    val savedBanner: Boolean = false,
    /** Correo nuevo al que se envió el enlace de confirmación. */
    val emailVerificationSentTo: String? = null,
    val showDeleteConfirm: Boolean = false,
    @StringRes val errorRes: Int? = null
)

sealed interface AccountSettingsEvent {
    data object AccountDeleted : AccountSettingsEvent
    data class ScrollToField(val field: FormField) : AccountSettingsEvent
}

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val updateProfilePhoto: UpdateProfilePhotoUseCase,
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
                            state.copy(initials = user.initials, photoUrl = user.photoUrl)
                        } else {
                            state.copy(
                                isLoaded = true,
                                initials = user.initials,
                                photoUrl = user.photoUrl,
                                fullName = user.fullName,
                                email = user.email,
                                canChangeEmail = updateProfile.canChangeEmail(),
                                latitude = user.latitude,
                                longitude = user.longitude
                            )
                        }
                    }
                }
        }
    }

    fun onFullNameChange(value: String) = _uiState.update {
        it.copy(fullName = value).edited().touch(FormField.FULL_NAME)
    }

    fun onEmailChange(value: String) = _uiState.update {
        it.copy(email = value).edited().touch(FormField.EMAIL)
    }

    fun onFieldFocusLost(field: FormField) = _uiState.update { it.touch(field) }

    /** Punto confirmado en el selector del mapa. */
    fun onChangeLocation(latitude: Double, longitude: Double) = _uiState.update {
        it.copy(latitude = latitude, longitude = longitude).edited()
    }

    /** Foto nueva (cámara o galería): se carga y se guarda de una vez, sin esperar a "Guardar cambios". */
    fun onPhotoPicked(localUri: String) {
        if (_uiState.value.photoUploadProgress != null) return
        _uiState.update { it.edited().copy(photoUploadProgress = 0f) }
        viewModelScope.launch {
            updateProfilePhoto(localUri) { progress ->
                _uiState.update { it.copy(photoUploadProgress = progress) }
            }
                .onSuccess { url ->
                    _uiState.update { it.copy(photoUploadProgress = null, photoUrl = url, photoSavedBanner = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(photoUploadProgress = null, errorRes = error.toMessageRes()) }
                }
        }
    }

    fun onRemovePhoto() {
        if (_uiState.value.photoUploadProgress != null) return
        _uiState.update { it.edited().copy(isSaving = true) }
        viewModelScope.launch {
            updateProfilePhoto.remove()
                .onSuccess { _uiState.update { it.copy(isSaving = false, photoUrl = "", photoSavedBanner = true) } }
                .onFailure { error -> _uiState.update { it.copy(isSaving = false, errorRes = error.toMessageRes()) } }
        }
    }

    fun onSave() {
        val s = _uiState.value
        if (s.isSaving || s.photoUploadProgress != null) return
        val errors = s.validate()
        if (errors.isNotEmpty()) {
            showFieldErrors(errors)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorRes = null) }
            updateProfile(s.fullName, s.email, s.latitude, s.longitude)
                .onSuccess { sentTo ->
                    _uiState.update {
                        it.copy(isSaving = false, savedBanner = true, emailVerificationSentTo = sentTo)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    val fieldErrors = error.fieldErrors
                    if (fieldErrors.isNotEmpty()) {
                        showFieldErrors(fieldErrors)
                    } else {
                        _uiState.update { it.copy(errorRes = error.toMessageRes()) }
                    }
                }
        }
    }

    fun onRequestDelete() = _uiState.update { it.copy(showDeleteConfirm = true) }

    fun onDismissDelete() = _uiState.update { it.copy(showDeleteConfirm = false) }

    fun onConfirmDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false, isSaving = true, errorRes = null) }
        viewModelScope.launch {
            deleteAccount()
                .onSuccess { _events.send(AccountSettingsEvent.AccountDeleted) }
                .onFailure { error -> _uiState.update { it.copy(isSaving = false, errorRes = error.toMessageRes()) } }
        }
    }

    private fun showFieldErrors(errors: Map<FormField, FieldError>) {
        _uiState.update { it.copy(form = it.form.touchAll(it.validate() + errors)) }
        AuthForms.PROFILE_ORDER.firstOrNull { it in errors }?.let {
            _events.trySend(AccountSettingsEvent.ScrollToField(it))
        }
    }

    private fun AccountSettingsUiState.validate() = AuthForms.profile(fullName, email)

    private fun AccountSettingsUiState.touch(field: FormField) = copy(form = form.touch(field, validate()))

    private fun AccountSettingsUiState.edited() =
        copy(savedBanner = false, photoSavedBanner = false, emailVerificationSentTo = null, errorRes = null)
}
