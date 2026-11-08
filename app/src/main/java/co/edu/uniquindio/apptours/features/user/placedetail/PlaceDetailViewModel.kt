package co.edu.uniquindio.apptours.features.user.placedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.common.FormState
import co.edu.uniquindio.apptours.core.common.RetryTrigger
import co.edu.uniquindio.apptours.core.util.fieldErrors
import co.edu.uniquindio.apptours.core.util.toMessageRes
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import co.edu.uniquindio.apptours.domain.usecase.comment.AddCommentUseCase
import co.edu.uniquindio.apptours.domain.usecase.comment.GetCommentsUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.DeletePlaceUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetMyVotesUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.GetPlaceByIdUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.MarkPlaceResolvedUseCase
import co.edu.uniquindio.apptours.domain.usecase.place.ToggleInterestUseCase
import co.edu.uniquindio.apptours.domain.usecase.user.GetCurrentUserUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.GetMyVisitsUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.ShareVisitExperienceUseCase
import co.edu.uniquindio.apptours.domain.usecase.visit.ToggleVisitedUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import co.edu.uniquindio.apptours.navigation.PlaceDetail
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
class PlaceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPlaceById: GetPlaceByIdUseCase,
    getComments: GetCommentsUseCase,
    getCurrentUser: GetCurrentUserUseCase,
    getMyVotes: GetMyVotesUseCase,
    getMyVisits: GetMyVisitsUseCase,
    private val toggleInterest: ToggleInterestUseCase,
    private val toggleVisited: ToggleVisitedUseCase,
    private val shareVisitExperience: ShareVisitExperienceUseCase,
    private val addComment: AddCommentUseCase,
    private val markPlaceResolved: MarkPlaceResolvedUseCase,
    private val deletePlace: DeletePlaceUseCase
) : ViewModel() {

    private val placeId = savedStateHandle.toRoute<PlaceDetail>().placeId

    /** Lo que solo vive en la pantalla (lo que se escribe, diálogos abiertos, errores de acciones). */
    private data class LocalState(
        val commentText: String = "",
        val commentForm: FormState = FormState(),
        val isSendingComment: Boolean = false,
        val experience: VisitExperienceState? = null,
        val showDeleteDialog: Boolean = false,
        val isDeleting: Boolean = false,
        val errorRes: Int? = null
    )

    private val local = MutableStateFlow(LocalState())
    private val retryTrigger = RetryTrigger()

    private val _events = Channel<PlaceDetailEvent>(Channel.BUFFERED)
    val events: Flow<PlaceDetailEvent> = _events.receiveAsFlow()

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
        state.copy(
            commentText = local.commentText,
            commentForm = local.commentForm,
            isSendingComment = local.isSendingComment,
            experience = local.experience,
            showDeleteDialog = local.showDeleteDialog,
            isDeleting = local.isDeleting,
            errorRes = local.errorRes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlaceDetailUiState())

    fun onRetry() = retryTrigger.retry()

    // ---- Comentarios ----

    fun onCommentTextChange(text: String) = local.update {
        it.copy(
            commentText = text,
            commentForm = it.commentForm.touch(FormField.COMMENT, PlaceForms.comment(text)),
            errorRes = null
        )
    }

    /** Si el campo se deja vacío sin haber escrito, no se marca en rojo (solo al intentar enviar). */
    fun onCommentFocusLost() = local.update {
        if (it.commentText.isEmpty()) it
        else it.copy(commentForm = it.commentForm.touch(FormField.COMMENT, PlaceForms.comment(it.commentText)))
    }

    fun onSendComment() {
        val s = local.value
        if (s.isSendingComment) return
        val errors = PlaceForms.comment(s.commentText)
        if (errors.isNotEmpty()) {
            local.update { it.copy(commentForm = it.commentForm.touchAll(errors)) }
            _events.trySend(PlaceDetailEvent.ScrollToField(FormField.COMMENT))
            return
        }
        local.update { it.copy(isSendingComment = true, errorRes = null) }
        viewModelScope.launch {
            val result = addComment(placeId, s.commentText)
            local.update { it.copy(isSendingComment = false) }
            result
                .onSuccess { local.update { it.copy(commentText = "", commentForm = FormState()) } }
                .onFailure { error ->
                    val fieldErrors = error.fieldErrors
                    if (fieldErrors.isNotEmpty()) {
                        local.update { it.copy(commentForm = it.commentForm.touchAll(fieldErrors)) }
                    } else {
                        local.update { it.copy(errorRes = error.toMessageRes()) }
                    }
                }
        }
    }

    // ---- "Me interesa", "Visitado" y experiencia ----

    fun onToggleInterest() = launchAction { toggleInterest(placeId) }

    /** Al marcarlo como visitado se ofrece contar la experiencia (es opcional: se puede cerrar). */
    fun onToggleVisited() {
        viewModelScope.launch {
            toggleVisited(placeId)
                .onSuccess { nowVisited ->
                    if (nowVisited) local.update { it.copy(experience = VisitExperienceState()) }
                }
                .onFailure { error -> local.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }

    fun onOpenExperience() = local.update { it.copy(experience = VisitExperienceState()) }

    fun onDismissExperience() = local.update { it.copy(experience = null) }

    fun onExperienceTextChange(text: String) = updateExperience(FormField.COMMENT) {
        it.copy(text = text.take(PlaceForms.MAX_COMMENT_LENGTH + 1))
    }

    fun onExperienceRatingChange(rating: Int) = updateExperience(FormField.RATING) { it.copy(rating = rating) }

    fun onSubmitExperience() {
        val draft = local.value.experience ?: return
        if (draft.isSaving) return
        val errors = PlaceForms.experience(draft.rating, draft.text)
        // El diálogo es corto: todos sus campos quedan a la vista, basta con marcarlos en rojo.
        if (errors.isNotEmpty()) {
            local.update { s -> s.copy(experience = s.experience?.copy(form = draft.form.touchAll(errors))) }
            return
        }
        local.update { s -> s.copy(experience = s.experience?.copy(isSaving = true)) }
        viewModelScope.launch {
            shareVisitExperience(placeId, draft.text, draft.rating)
                .onSuccess { local.update { it.copy(experience = null) } }
                .onFailure { error ->
                    local.update { s ->
                        s.copy(
                            experience = s.experience?.let { e ->
                                e.copy(isSaving = false, form = e.form.touchAll(error.fieldErrors))
                            },
                            errorRes = error.toMessageRes().takeIf { error.fieldErrors.isEmpty() }
                        )
                    }
                }
        }
    }

    private fun updateExperience(field: FormField, transform: (VisitExperienceState) -> VisitExperienceState) {
        local.update { s ->
            val current = s.experience ?: return@update s
            val edited = transform(current)
            s.copy(experience = edited.copy(form = edited.form.touch(field, PlaceForms.experience(edited.rating, edited.text))))
        }
    }

    // ---- Acciones del autor ----

    fun onMarkResolved() = launchAction { markPlaceResolved(placeId) }

    fun onDeleteClick() = local.update { it.copy(showDeleteDialog = true) }

    fun onDismissDelete() = local.update { it.copy(showDeleteDialog = false) }

    fun onConfirmDelete() {
        local.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            val result = deletePlace(placeId)
            local.update { it.copy(isDeleting = false) }
            result
                .onSuccess { _events.send(PlaceDetailEvent.Deleted) }
                .onFailure { error -> local.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }

    private fun launchAction(action: suspend () -> Result<*>) {
        viewModelScope.launch {
            action().onFailure { error -> local.update { it.copy(errorRes = error.toMessageRes()) } }
        }
    }
}
