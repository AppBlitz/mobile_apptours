package co.edu.uniquindio.apptours.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.errorFor
import co.edu.uniquindio.apptours.core.util.invalidFields
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val register: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events: Flow<RegisterEvent> = _events.receiveAsFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null, registerError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, registerError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, registerError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value, confirmPasswordError = null, registerError = null)
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onPickLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng, locationPicked = true) }
    }

    fun onTermsChange(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted, termsError = null) }
    }

    fun onSubmit() {
        val s = _uiState.value
        if (s.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registerError = null) }
            register(
                fullName = s.fullName,
                email = s.email,
                password = s.password,
                passwordConfirmation = s.confirmPassword,
                termsAccepted = s.termsAccepted,
                latitude = s.latitude,
                longitude = s.longitude
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.Registered)
            }.onFailure { error ->
                val fields = error.invalidFields
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        fullNameError = fields.errorFor(FormField.FULL_NAME),
                        emailError = fields.errorFor(FormField.EMAIL),
                        passwordError = fields.errorFor(FormField.PASSWORD, R.string.error_password_min_length),
                        confirmPasswordError = fields.errorFor(FormField.PASSWORD_CONFIRMATION),
                        termsError = fields.errorFor(FormField.TERMS),
                        registerError = if (fields.isEmpty()) error.toMessageRes() else null
                    )
                }
            }
        }
    }
}
