package co.edu.uniquindio.apptours.features.auth.login

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val form: FormState = FormState(),
    /** Error que no es de un campo (credenciales, red): va en el banner junto al botón. */
    @StringRes val authError: Int? = null,
    val isLoading: Boolean = false
)

sealed interface LoginEvent {
    data class LoggedIn(val role: UserRole) : LoginEvent
    data class ScrollToField(val field: FormField) : LoginEvent
}
