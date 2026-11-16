package co.edu.uniquindio.apptours.features.moderator.queue

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.ModerationFilter
import co.edu.uniquindio.apptours.domain.model.ModerationStats
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.usecase.moderation.GetModerationQueueUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.GetModerationStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ModerationQueueUiState(
    val filter: ModerationFilter = ModerationFilter.PENDIENTES,
    val places: List<Place> = emptyList(),
    val stats: ModerationStats = ModerationStats(),
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModerationQueueViewModel @Inject constructor(
    getModerationQueue: GetModerationQueueUseCase,
    getModerationStats: GetModerationStatsUseCase
) : ViewModel() {

    private val filter = MutableStateFlow(ModerationFilter.PENDIENTES)
    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<ModerationQueueUiState> = retryTrigger.restartable {
        combine(
            filter,
            filter.flatMapLatest { getModerationQueue(it) },
            getModerationStats()
        ) { filter, places, stats ->
            ModerationQueueUiState(filter = filter, places = places, stats = stats, isLoading = false)
        }.catch { emit(ModerationQueueUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModerationQueueUiState())

    fun onRetry() = retryTrigger.retry()

    fun onFilterSelected(value: ModerationFilter) {
        filter.value = value
    }
}
