package co.edu.uniquindio.apptours.features.moderator.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ModeratorBottomTab { POR_REVISAR, HISTORIAL, CUENTA }

sealed interface ModeratorHomeEvent {
    data object LoggedOut : ModeratorHomeEvent
}

@HiltViewModel
class ModeratorHomeViewModel @Inject constructor(
    getCurrentUser: GetCurrentUserUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    val currentUser: StateFlow<User?> = getCurrentUser()
        .catch { emit(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _events = Channel<ModeratorHomeEvent>(Channel.BUFFERED)
    val events: Flow<ModeratorHomeEvent> = _events.receiveAsFlow()

    fun onLogout() {
        viewModelScope.launch {
            logout()
            _events.send(ModeratorHomeEvent.LoggedOut)
        }
    }
}
