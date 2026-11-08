package co.edu.uniquindio.apptours.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Decide el destino inicial: sin sesión ⇒ AuthGraph; con sesión ⇒ grafo según el rol. */
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getSession: GetSessionUseCase
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val user = getSession.restore()
            _startDestination.value = when (user?.role) {
                null -> AuthGraph
                UserRole.MODERATOR -> ModeratorGraph
                UserRole.USER -> UserGraph
            }
        }
    }
}
