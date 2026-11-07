package co.edu.uniquindio.apptours.features.auth.register

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation

data class RegisterUiState(
    val fullName: String = "",
    @StringRes val fullNameError: Int? = null,
    val email: String = "",
    @StringRes val emailError: Int? = null,
    val password: String = "",
    @StringRes val passwordError: Int? = null,
    val confirmPassword: String = "",
    @StringRes val confirmPasswordError: Int? = null,
    val passwordVisible: Boolean = false,
    val latitude: Double = DefaultLocation.LATITUDE,
    val longitude: Double = DefaultLocation.LONGITUDE,
    val locationPicked: Boolean = false,
    val termsAccepted: Boolean = false,
    @StringRes val termsError: Int? = null,
    @StringRes val registerError: Int? = null,
    val isLoading: Boolean = false
)

sealed interface RegisterEvent {
    data object Registered : RegisterEvent
}
