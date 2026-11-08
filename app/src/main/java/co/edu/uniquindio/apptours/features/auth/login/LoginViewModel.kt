package co.edu.uniquindio.apptours.features.auth.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.usecase.auth.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, authError = null).touch(FormField.EMAIL) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, authError = null).touch(FormField.PASSWORD) }
    }

    fun onFieldFocusLost(field: FormField) {
        _uiState.update { it.touch(field) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onSubmit() {
        val state = _uiState.value
        if (state.isLoading) return
        val errors = AuthForms.login(state.email, state.password)
        if (errors.isNotEmpty()) {
            showFieldErrors(errors)
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            handleResult(login(state.email, state.password))
        }
    }

    fun onGoogleToken(idToken: String) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }
            handleResult(signInWithGoogle(idToken))
        }
    }

    fun onGoogleError(@StringRes messageRes: Int) {
        _uiState.update { it.copy(authError = messageRes) }
    }

    private suspend fun handleResult(result: Result<User>) {
        result
            .onSuccess { user ->
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.LoggedIn(user.role))
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                val fieldErrors = error.fieldErrors
                if (fieldErrors.isNotEmpty()) {
                    showFieldErrors(fieldErrors)
                } else {
                    _uiState.update { it.copy(authError = error.toMessageRes()) }
                }
            }
    }

    private fun showFieldErrors(errors: Map<FormField, FieldError>) {
        _uiState.update { it.copy(form = it.form.touchAll(errors)) }
        _uiState.value.form.firstInvalid(AuthForms.LOGIN_ORDER)?.let {
            _events.trySend(LoginEvent.ScrollToField(it))
        }
    }

    private fun LoginUiState.touch(field: FormField) =
        copy(form = form.touch(field, AuthForms.login(email, password)))
}
