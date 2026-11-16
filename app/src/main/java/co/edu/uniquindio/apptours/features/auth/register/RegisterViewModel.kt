package co.edu.uniquindio.apptours.features.auth.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.usecase.auth.RegisterUseCase
import co.edu.uniquindio.apptours.domain.usecase.auth.SignInWithGoogleUseCase
import co.edu.uniquindio.apptours.domain.validation.AuthForms
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
    private val register: RegisterUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events: Flow<RegisterEvent> = _events.receiveAsFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, registerError = null).touch(FormField.FULL_NAME) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, registerError = null).touch(FormField.EMAIL) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, registerError = null).touch(FormField.PASSWORD) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(confirmPassword = value, registerError = null).touch(FormField.PASSWORD_CONFIRMATION)
        }
    }

    fun onFieldFocusLost(field: FormField) {
        _uiState.update { it.touch(field) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onPickLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng, locationPicked = true) }
    }

    fun onTermsChange(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted).touch(FormField.TERMS) }
    }

    fun onSubmit() {
        val s = _uiState.value
        if (s.isLoading) return
        val errors = s.validate()
        if (errors.isNotEmpty()) {
            showFieldErrors(errors)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registerError = null) }
            handleResult(
                register(
                    fullName = s.fullName,
                    email = s.email,
                    password = s.password,
                    passwordConfirmation = s.confirmPassword,
                    termsAccepted = s.termsAccepted,
                    latitude = s.latitude,
                    longitude = s.longitude
                )
            )
        }
    }

    /** Registrarse con Google también exige aceptar los términos; el resto del formulario no aplica. */
    fun onGoogleClick() {
        val s = _uiState.value
        if (s.isLoading) return
        val termsError = Validators.accepted(s.termsAccepted)
        if (termsError != null) {
            showFieldErrors(mapOf(FormField.TERMS to termsError), keepOthers = true)
            return
        }
        _uiState.update { it.copy(registerError = null) }
        _events.trySend(RegisterEvent.LaunchGoogleSignIn)
    }

    fun onGoogleToken(idToken: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registerError = null) }
            handleResult(signInWithGoogle(idToken))
        }
    }

    fun onGoogleError(@StringRes messageRes: Int) {
        _uiState.update { it.copy(registerError = messageRes) }
    }

    private suspend fun handleResult(result: Result<User>) {
        result
            .onSuccess { user ->
                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.Registered(user.role))
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                val fieldErrors = error.fieldErrors
                if (fieldErrors.isNotEmpty()) {
                    showFieldErrors(fieldErrors)
                } else {
                    _uiState.update { it.copy(registerError = error.toMessageRes()) }
                }
            }
    }

    /**
     * Marca en rojo los campos inválidos y lleva la pantalla al primero.
     * Con [keepOthers] solo se agregan esos errores, sin marcar los demás campos del formulario.
     */
    private fun showFieldErrors(errors: Map<FormField, FieldError>, keepOthers: Boolean = false) {
        _uiState.update { state ->
            val form = if (keepOthers) {
                state.form.copy(
                    fieldErrors = state.validate(),
                    touchedFields = state.form.touchedFields + errors.keys
                )
            } else {
                state.form.touchAll(state.validate() + errors)
            }
            state.copy(form = form)
        }
        AuthForms.REGISTER_ORDER.firstOrNull { it in errors }?.let {
            _events.trySend(RegisterEvent.ScrollToField(it))
        }
    }

    private fun RegisterUiState.validate() =
        AuthForms.register(fullName, email, password, confirmPassword, termsAccepted)

    private fun RegisterUiState.touch(field: FormField) = copy(form = form.touch(field, validate()))
}
