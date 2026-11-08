package co.edu.uniquindio.apptours.features.user.placedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.apptours.domain.usecase.comment.GetCommentsUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetMyVotesUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.MarkPlaceResolvedUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.ToggleInterestUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.GetMyVisitsUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.ToggleVisitedUseCase
import co.edu.uniquindio.apptours.navigation.PlaceDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaceById: GetPlaceByIdUseCase,
    getComments: GetCommentsUseCase,
    getCurrentUser: GetCurrentUserUseCase,
    getMyVotes: GetMyVotesUseCase,
    getMyVisits: GetMyVisitsUseCase,
    private val toggleInterest: ToggleInterestUseCase,
    private val toggleVisited: ToggleVisitedUseCase,
    private val addComment: AddCommentUseCase,
    private val markPlaceResolved: MarkPlaceResolvedUseCase
) : ViewModel() {

    private val placeId = savedStateHandle.toRoute<PlaceDetail>().placeId

    private data class LocalState(val commentText: String = "", val errorRes: Int? = null)

    private val local = MutableStateFlow(LocalState())
    private val retryTrigger = RetryTrigger()

    private val remote = retryTrigger.restartable {
        combine(
            getPlaceById(placeId),
            getComments(placeId),
            getCurrentUser(),
            getMyVotes(),
            getMyVisits()
        ) { place, comments, user, votes, visits ->
            PlaceDetailUiState(
                isLoading = false,
                place = place,
                comments = comments,
                currentUserId = user?.id,
                userLatitude = user?.latitude ?: DefaultLocation.LATITUDE,
                userLongitude = user?.longitude ?: DefaultLocation.LONGITUDE,
                isInterested = placeId in votes,
                isVisited = visits.any { it.placeId == placeId }
            )
        }.catch { emit(PlaceDetailUiState(isLoading = false, loadErrorRes = it.toMessageRes())) }
    }

    val uiState: StateFlow<PlaceDetailUiState> = combine(remote, local) { state, local ->
        state.copy(commentText = local.commentText, errorRes = local.errorRes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaceDetailUiState())

    fun onRetry() = retryTrigger.retry()

    fun onCommentTextChange(text: String) = local.update { it.copy(commentText = text, errorRes = null) }

    fun onSendComment() {
        val text = local.value.commentText
        viewModelScope.launch {
            addComment(placeId, text)
                .onSuccess { local.update { LocalState() } }
                .onFailure { error -> local.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }

    fun onToggleInterest() = launchAction { toggleInterest(placeId) }

    fun onToggleVisited() = launchAction { toggleVisited(placeId) }

    fun onMarkResolved() = launchAction { markPlaceResolved(placeId) }

    private fun launchAction(action: suspend () -> Result<*>) {
        viewModelScope.launch {
            action().onFailure { error -> local.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }
}
