package co.edu.uniquindio.apptours.features.user.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.Badge
import co.edu.uniquindio.apptours.domain.model.BadgeId
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserStats
import co.edu.uniquindio.apptours.domain.usecase.auth.LogoutUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.CalculateLevelUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.GetUserBadgesUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetUserStatsUseCase
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

data class ProfileUiState(
    val user: User? = null,
    val stats: UserStats = UserStats(),
    /** Avance (0..1) hacia el siguiente nivel. */
    val levelProgress: Float = 0f,
    val badges: List<Badge> = emptyList(),
    val selectedBadgeId: BadgeId? = null,
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
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
    getUserBadges: GetUserBadgesUseCase,
    private val calculateLevel: CalculateLevelUseCase,
    evaluateBadges: EvaluateBadgesUseCase,
    private val logout: LogoutUseCase
) : ViewModel() {

    private val selectedBadgeId = MutableStateFlow<BadgeId?>(null)
    private val retryTrigger = RetryTrigger()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<ProfileUiState> = retryTrigger.restartable {
        combine(getCurrentUser(), getUserStats(), getUserBadges(), selectedBadgeId) { user, stats, badges, selected ->
            ProfileUiState(
                isLoading = false,
                user = user,
                stats = stats,
                levelProgress = calculateLevel.progressToNext(user?.points ?: 0),
                // Las ganadas primero (las más recientes arriba), luego las que faltan por progreso.
                badges = badges.sortedWith(
                    compareByDescending<Badge> { it.unlocked }
                        .thenByDescending { it.earnedAt ?: 0L }
                        .thenByDescending { it.progressFraction }
                ),
                selectedBadgeId = selected
            )
        }.catch { emit(ProfileUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    init {
        viewModelScope.launch { evaluateBadges() }
    }

    fun onRetry() = retryTrigger.retry()

    fun onSelectBadge(badgeId: BadgeId) {
        selectedBadgeId.value = badgeId
    }

    fun onLogout() {
        viewModelScope.launch {
            logout()
            _events.send(ProfileEvent.LoggedOut)
        }
    }
}
