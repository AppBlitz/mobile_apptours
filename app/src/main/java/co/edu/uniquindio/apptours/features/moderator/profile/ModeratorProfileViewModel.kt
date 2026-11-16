package co.edu.uniquindio.apptours.features.moderator.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.TrophyId
import co.edu.uniquindio.apptours.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.GetModeratorProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModeratorProfileViewModel @Inject constructor(
    getModeratorProfile: GetModeratorProfileUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val selectedTrophy = MutableStateFlow<TrophyId?>(null)
    private val retryTrigger = RetryTrigger()

    private val _events = Channel<ModeratorProfileEvent>(Channel.BUFFERED)
    val events: Flow<ModeratorProfileEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<ModeratorProfileUiState> = retryTrigger.restartable {
        combine(getModeratorProfile(), selectedTrophy) { profile, selected ->
            ModeratorProfileUiState(profile = profile, selectedTrophy = selected, isLoading = false)
        }.catch { emit(ModeratorProfileUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModeratorProfileUiState())

    fun onRetry() = retryTrigger.retry()

    fun onSelectTrophy(id: TrophyId) {
        selectedTrophy.value = id
    }

    fun onLogout() {
        viewModelScope.launch {
            logout()
            _events.send(ModeratorProfileEvent.LoggedOut)
        }
    }
}
