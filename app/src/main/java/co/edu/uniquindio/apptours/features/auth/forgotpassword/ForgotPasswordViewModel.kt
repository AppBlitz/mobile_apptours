package co.edu.uniquindio.apptours.features.auth.forgotpassword

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.usecase.auth.SendPasswordResetUseCase
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

data class ForgotPasswordUiState(
    val email: String = "",
    val form: FormState = FormState(),
    /** Correo al que se envió el enlace; null mientras no se haya enviado. */
    val sentToEmail: String? = null,
    /** Error que no es del campo (sin red, demasiados intentos). */
    @StringRes val errorRes: Int? = null,
    val isLoading: Boolean = false
)

sealed interface ForgotPasswordEvent {
    data class ScrollToField(val field: FormField) : ForgotPasswordEvent
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordReset: SendPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>(Channel.BUFFERED)
    val events: Flow<ForgotPasswordEvent> = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                sentToEmail = null,
                errorRes = null,
                form = it.form.touch(FormField.EMAIL, AuthForms.passwordReset(value))
            )
        }
    }

    fun onEmailFocusLost() {
        _uiState.update { it.copy(form = it.form.touch(FormField.EMAIL, AuthForms.passwordReset(it.email))) }
    }

    fun onSend() {
        val email = _uiState.value.email
        if (_uiState.value.isLoading) return
        val errors = AuthForms.passwordReset(email)
        if (errors.isNotEmpty()) {
            showFieldErrors(errors)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            sendPasswordReset(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, sentToEmail = email.trim()) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    val fieldErrors = error.fieldErrors
                    if (fieldErrors.isNotEmpty()) {
                        showFieldErrors(fieldErrors)
                    } else {
                        _uiState.update { it.copy(errorRes = error.toMessageRes()) }
                    }
                }
        }
    }

    private fun showFieldErrors(errors: Map<FormField, FieldError>) {
        _uiState.update { it.copy(form = it.form.touchAll(errors)) }
        _events.trySend(ForgotPasswordEvent.ScrollToField(FormField.EMAIL))
    }
}
