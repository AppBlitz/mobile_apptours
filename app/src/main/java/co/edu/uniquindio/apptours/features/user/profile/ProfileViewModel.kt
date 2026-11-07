package co.edu.uniquindio.apptours.features.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.domain.model.Badge
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserStats
import co.edu.uniquindio.apptours.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.GetUserBadgesUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val stats: UserStats = UserStats(),
    val badges: List<Badge> = emptyList(),
    val selectedBadgeId: String? = null
) {
    val selectedBadge: Badge?
        get() = badges.find { it.id == selectedBadgeId } ?: badges.firstOrNull()
}

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getCurrentUser: GetCurrentUserUseCase,
    getUserStats: GetUserStatsUseCase,
    private val getUserBadges: GetUserBadgesUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val selectedBadgeId = MutableStateFlow<String?>(null)

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<ProfileUiState> =
        combine(getCurrentUser(), getUserStats(), selectedBadgeId) { user, stats, selected ->
            ProfileUiState(
                user = user,
                stats = stats,
                badges = getUserBadges(points = user?.points ?: 0, verifiedCount = stats.verifiedCount),
                selectedBadgeId = selected
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun onSelectBadge(badgeId: String) {
        selectedBadgeId.value = badgeId
    }

    fun onLogout() {
        viewModelScope.launch {
            logout()
            _events.send(ProfileEvent.LoggedOut)
        }
    }
}
