package co.edu.uniquindio.apptours.features.moderator.history

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.usecase.moderation.GetModerationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ModerationHistoryUiState(
    val entries: List<Place> = emptyList(),
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

@HiltViewModel
class ModerationHistoryViewModel @Inject constructor(
    getModerationHistory: GetModerationHistoryUseCase
) : ViewModel() {

    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<ModerationHistoryUiState> = retryTrigger.restartable {
        getModerationHistory().map { entries ->
            ModerationHistoryUiState(
                entries = entries,
                approvedCount = entries.count { it.status != PlaceStatus.RECHAZADA },
                rejectedCount = entries.count { it.status == PlaceStatus.RECHAZADA },
                isLoading = false
            )
        }.catch { emit(ModerationHistoryUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModerationHistoryUiState())

    fun onRetry() = retryTrigger.retry()
}
