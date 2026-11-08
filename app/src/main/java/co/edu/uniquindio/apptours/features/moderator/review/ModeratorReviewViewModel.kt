package co.edu.uniquindio.apptours.features.moderator.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.usecase.comment.GetCommentsUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.RejectPlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.moderation.VerifyPlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.MarkPlaceResolvedUseCase
import co.edu.uniquindio.apptours.domain.validation.ModerationForms
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

    fun onToggleCheck(item: ChecklistItem) = local.update {
        val checked = if (item in it.checkedItems) it.checkedItems - item else it.checkedItems + item
        it.copy(checkedItems = checked, errorRes = null)
    }

    fun onOpenReject() = local.update { it.copy(showRejectSheet = true, errorRes = null) }

    fun onDismissReject() = local.update {
        it.copy(showRejectSheet = false, rejectionReason = "", rejectForm = FormState(), errorRes = null)
    }

    fun onReasonChange(reason: String) = local.update {
        val value = reason.take(ModerationForms.MAX_REASON_LENGTH)
        it.copy(
            rejectionReason = value,
            rejectForm = it.rejectForm.touch(FormField.REJECTION_REASON, ModerationForms.rejection(value)),
            errorRes = null
        )
    }

    /** Un motivo rápido se agrega al texto para que el moderador lo complete, no lo reemplaza. */
    fun onQuickReason(text: String) {
        val current = local.value.rejectionReason.trim()
        onReasonChange(if (current.isEmpty()) "$text: " else "$current $text.")
    }

    fun onReasonFocusLost() = local.update {
        it.copy(rejectForm = it.rejectForm.touch(FormField.REJECTION_REASON, ModerationForms.rejection(it.rejectionReason)))
    }

    fun onVerify() {
        if (!local.value.canVerify) return
        process { verifyPlace(placeId) }
    }

    fun onMarkResolved() = process { markPlaceResolved(placeId) }

    fun onConfirmReject() {
        val errors = ModerationForms.rejection(local.value.rejectionReason)
        if (errors.isNotEmpty()) {
            showReasonErrors(errors)
            return
        }
        process { rejectPlace(placeId, local.value.rejectionReason) }
    }

    private fun showReasonErrors(errors: Map<FormField, FieldError>) {
        local.update { it.copy(rejectForm = it.rejectForm.touchAll(errors)) }
        _events.trySend(ModeratorReviewEvent.ScrollToField(FormField.REJECTION_REASON))
    }

    private fun process(action: suspend () -> Result<Unit>) {
        if (local.value.isProcessing) return
        viewModelScope.launch {
            local.update { it.copy(isProcessing = true, errorRes = null) }
            val result = action()
            local.update { it.copy(isProcessing = false) }
            result
                .onSuccess {
                    local.update { it.copy(showRejectSheet = false) }
                    _events.send(ModeratorReviewEvent.Done)
                }
                .onFailure { error ->
                    val fieldErrors = error.fieldErrors
                    if (fieldErrors.isNotEmpty()) {
                        showReasonErrors(fieldErrors)
                    } else {
                        local.update { it.copy(errorRes = error.toMessageRes()) }
                    }
                }
        }
    }
}
