package co.edu.uniquindio.apptours.features.register

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.usecase.ColabUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegisterUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val passwordVisible: Boolean = false,
    val latitude: Double = 4.5350,
    val longitude: Double = -75.6750,
    val locationPicked: Boolean = false,
    val termsAccepted: Boolean = false,
    val termsError: String? = null,
    val registerError: String? = null
)

class RegisterViewModel(
    private val useCases: ColabUseCases = ColabUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

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

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onPickLocationFromMap(lat: Double, lng: Double) {
        _uiState.update {
            it.copy(latitude = lat, longitude = lng, locationPicked = true)
        }
    }

    fun onTermsChange(accepted: Boolean) {
        _uiState.update { it.copy(termsAccepted = accepted, termsError = null) }
    }

    fun submitRegistration(onSuccess: (User) -> Unit) {
        val s = _uiState.value
        var valid = true

        if (s.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "El nombre completo es obligatorio") }
            valid = false
        }
        if (!Validators.isValidEmail(s.email)) {
            _uiState.update { it.copy(emailError = "Correo electrónico inválido") }
            valid = false
        }
        if (!Validators.isValidPassword(s.password)) {
            _uiState.update { it.copy(passwordError = "Mínimo 8 caracteres") }
            valid = false
        }
        if (s.password != s.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Las contraseñas no coinciden") }
            valid = false
        }
        if (!s.termsAccepted) {
            _uiState.update { it.copy(termsError = "Debes aceptar los términos y la política de privacidad") }
            valid = false
        }

        if (!valid) return

        val result = useCases.registerUser(
            fullName = s.fullName,
            email = s.email,
            password = s.password,
            latitude = s.latitude,
            longitude = s.longitude
        )

        result.fold(
            onSuccess = { user -> onSuccess(user) },
            onFailure = { err ->
                _uiState.update { it.copy(registerError = err.message ?: "Error al crear la cuenta") }
            }
        )
    }
}
