package co.edu.uniquindio.apptours.features.login

import androidx.lifecycle.ViewModel
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.usecase.ColabUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val authError: String? = null,
    val recoveryEmail: String = "",
    val recoveryError: String? = null,
    val recoverySuccessMessage: String? = null
)

class LoginViewModel(
    private val useCases: ColabUseCases = ColabUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, authError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, authError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun fillDemoCredentials(isModerator: Boolean) {
        if (isModerator) {
            _uiState.update {
                it.copy(
                    email = "moderador@colabtour.com",
                    password = "12345678",
                    emailError = null,
                    passwordError = null,
                    authError = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    email = "valentina@correo.com",
                    password = "12345678",
                    emailError = null,
                    passwordError = null,
                    authError = null
                )
            }
        }
    }

    fun submitLogin(onSuccess: (User) -> Unit) {
        val state = _uiState.value
        var valid = true

        if (!Validators.isValidEmail(state.email)) {
            _uiState.update { it.copy(emailError = "Ingresa un correo electrónico válido") }
            valid = false
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "La contraseña es obligatoria") }
            valid = false
        }
        if (!valid) return

        val result = useCases.login(state.email, state.password)
        result.fold(
            onSuccess = { user -> onSuccess(user) },
            onFailure = { error ->
                _uiState.update { it.copy(authError = error.message ?: "Error al iniciar sesión") }
            }
        )
    }

    fun onRecoveryEmailChange(value: String) {
        _uiState.update {
            it.copy(recoveryEmail = value, recoveryError = null, recoverySuccessMessage = null)
        }
    }

    fun sendRecoveryLink() {
        val email = _uiState.value.recoveryEmail
        if (!Validators.isValidEmail(email)) {
            _uiState.update { it.copy(recoveryError = "Escribe un correo electrónico válido") }
            return
        }
        val result = useCases.sendPasswordRecovery(email)
        result.fold(
            onSuccess = { msg ->
                _uiState.update { it.copy(recoverySuccessMessage = msg, recoveryError = null) }
            },
            onFailure = { err ->
                _uiState.update { it.copy(recoveryError = err.message) }
            }
        )
    }
}
