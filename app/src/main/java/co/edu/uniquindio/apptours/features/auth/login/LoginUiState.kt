package co.edu.uniquindio.apptours.features.auth.login

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    @StringRes val emailError: Int? = null,
    val password: String = "",
    @StringRes val passwordError: Int? = null,
    val passwordVisible: Boolean = false,
    @StringRes val authError: Int? = null,
    val isLoading: Boolean = false
)

sealed interface LoginEvent {
    data class LoggedIn(val role: UserRole) : LoginEvent
}
