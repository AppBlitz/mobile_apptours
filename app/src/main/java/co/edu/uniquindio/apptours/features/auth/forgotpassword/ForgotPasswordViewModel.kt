package co.edu.uniquindio.apptours.features.auth.forgotpassword

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.usecase.auth.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val email: String = "",
    @StringRes val emailError: Int? = null,
    /** Correo al que se envió el enlace; null mientras no se haya enviado. */
    val sentToEmail: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordReset: SendPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, sentToEmail = null) }
    }

    fun onSend() {
        val email = _uiState.value.email
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            sendPasswordReset(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, sentToEmail = email.trim(), emailError = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, emailError = error.toMessageRes()) }
                }
        }
    }
}
