package co.edu.uniquindio.apptours.features.auth.register

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.model.UserRole

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val locationPicked: Boolean = false,
    val termsAccepted: Boolean = false,
    val form: FormState = FormState(),
    /** Error que no es de un campo (correo ya registrado, red): va junto al botón. */
    @StringRes val registerError: Int? = null,
    val isLoading: Boolean = false
)

sealed interface RegisterEvent {
    /** Con Google puede entrar una cuenta que ya existía, por eso se navega según el rol. */
    data class Registered(val role: UserRole) : RegisterEvent
    data class ScrollToField(val field: FormField) : RegisterEvent
    /** Los términos ya están aceptados: la pantalla abre el selector de cuentas de Google. */
    data object LaunchGoogleSignIn : RegisterEvent
}
