package co.edu.uniquindio.apptours.features.moderator.review

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.Comment
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.usecase.comment.GetCommentsUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.RejectPlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.VerifyPlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.MarkPlaceResolvedUseCase
import co.edu.uniquindio.apptours.navigation.ModeratorReview
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ModeratorReviewUiState(
    val isLoading: Boolean = true,
    val place: Place? = null,
    val comments: List<Comment> = emptyList(),
    val checkLocationExists: Boolean = true,
    val checkSchedulePlausible: Boolean = true,
    val checkPhotosMatch: Boolean = true,
    val showRejectSheet: Boolean = false,
    val rejectionReason: String = DEFAULT_REJECTION_REASON,
    @StringRes val errorRes: Int? = null,
    @StringRes val loadErrorRes: Int? = null,
    val isProcessing: Boolean = false
) {
    companion object {
        const val DEFAULT_REJECTION_REASON =
            "No pudimos verificar que el lugar exista en la ubicación indicada. Agrega una foto de la fachada y corrige las coordenadas."
    }
}

sealed interface ModeratorReviewEvent {
    data object Done : ModeratorReviewEvent
}

@HiltViewModel
class ModeratorReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaceById: GetPlaceByIdUseCase,
    getComments: GetCommentsUseCase,
    private val verifyPlace: VerifyPlaceUseCase,
    private val rejectPlace: RejectPlaceUseCase,
    private val markPlaceResolved: MarkPlaceResolvedUseCase
) : ViewModel() {

    private val placeId = savedStateHandle.toRoute<ModeratorReview>().placeId

    private val local = MutableStateFlow(ModeratorReviewUiState())
    private val retryTrigger = RetryTrigger()

    val uiState: StateFlow<ModeratorReviewUiState> = retryTrigger.restartable {
        combine(local, getPlaceById(placeId), getComments(placeId)) { state, place, comments ->
            state.copy(isLoading = false, place = place, comments = comments)
        }.catch { emit(ModeratorReviewUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModeratorReviewUiState())

    private val _events = Channel<ModeratorReviewEvent>(Channel.BUFFERED)
    val events: Flow<ModeratorReviewEvent> = _events.receiveAsFlow()

    fun onRetry() = retryTrigger.retry()

    fun onCheckLocationChange(checked: Boolean) = local.update { it.copy(checkLocationExists = checked) }

    fun onCheckScheduleChange(checked: Boolean) = local.update { it.copy(checkSchedulePlausible = checked) }

    fun onCheckPhotosChange(checked: Boolean) = local.update { it.copy(checkPhotosMatch = checked) }

    fun onOpenReject() = local.update { it.copy(showRejectSheet = true, errorRes = null) }

    fun onDismissReject() = local.update { it.copy(showRejectSheet = false, errorRes = null) }

    fun onReasonChange(reason: String) = local.update { it.copy(rejectionReason = reason, errorRes = null) }

    fun onVerify() = process { verifyPlace(placeId) }

    fun onMarkResolved() = process { markPlaceResolved(placeId) }

    fun onConfirmReject() = process { rejectPlace(placeId, local.value.rejectionReason) }

    private fun process(action: suspend () -> Result<Unit>) {
        if (local.value.isProcessing) return
        viewModelScope.launch {
            local.update { it.copy(isProcessing = true) }
            action()
                .onSuccess {
                    local.update { it.copy(isProcessing = false, showRejectSheet = false) }
                    _events.send(ModeratorReviewEvent.Done)
                }
                .onFailure { error ->
                    local.update { it.copy(isProcessing = false, errorRes = error.toMessageRes()) }
                }
        }
    }
}
