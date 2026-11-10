package co.edu.uniquindio.apptours.features.user.placedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.components.DeletePlaceDialog
import co.edu.uniquindio.apptours.core.designsystem.components.ErrorMessage
import co.edu.uniquindio.apptours.core.designsystem.components.LoadingOverlay
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.DistanceCalculator
import co.edu.uniquindio.apptours.core.util.messageOf
import co.edu.uniquindio.apptours.features.user.placedetail.components.*

@Composable
fun PlaceDetailRoute(
    onNavigateBack: () -> Unit,
    onEditPlace: (String) -> Unit,
    onPlaceDeleted: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PlaceDetailEvent.Deleted -> onPlaceDeleted()
                is PlaceDetailEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    PlaceDetailScreen(
        state = state,
        requesters = requesters,
        actions = PlaceDetailActionsHandler(
            onNavigateBack = onNavigateBack,
            onEditPlace = { state.place?.let { onEditPlace(it.id) } },
            onToggleInterest = viewModel::onToggleInterest,
            onToggleVisited = viewModel::onToggleVisited,
            onOpenExperience = viewModel::onOpenExperience,
            onMarkResolved = viewModel::onMarkResolved,
            onDeleteClick = viewModel::onDeleteClick,
            onConfirmDelete = viewModel::onConfirmDelete,
            onDismissDelete = viewModel::onDismissDelete,
            onCommentTextChange = viewModel::onCommentTextChange,
            onCommentFocusLost = viewModel::onCommentFocusLost,
            onSendComment = viewModel::onSendComment,
            onExperienceRatingChange = viewModel::onExperienceRatingChange,
            onExperienceTextChange = viewModel::onExperienceTextChange,
            onSubmitExperience = viewModel::onSubmitExperience,
            onDismissExperience = viewModel::onDismissExperience,
            onRetry = viewModel::onRetry
        )
    )
}

/** Eventos de la pantalla de detalle agrupados. */
data class PlaceDetailActionsHandler(
    val onNavigateBack: () -> Unit,
    val onEditPlace: () -> Unit,
    val onToggleInterest: () -> Unit,
    val onToggleVisited: () -> Unit,
    val onOpenExperience: () -> Unit,
    val onMarkResolved: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onConfirmDelete: () -> Unit,
    val onDismissDelete: () -> Unit,
    val onCommentTextChange: (String) -> Unit,
    val onCommentFocusLost: () -> Unit,
    val onSendComment: () -> Unit,
    val onExperienceRatingChange: (Int) -> Unit,
    val onExperienceTextChange: (String) -> Unit,
    val onSubmitExperience: () -> Unit,
    val onDismissExperience: () -> Unit,
    val onRetry: () -> Unit
)

@Composable
fun PlaceDetailScreen(
    state: PlaceDetailUiState,
    requesters: FieldRequesters,
    actions: PlaceDetailActionsHandler
) {
    val place = state.place
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = actions.onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (place == null) {
        if (state.isLoading || state.isDeleting) {
            LoadingOverlay(modifier = Modifier.background(ColabBackground))
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.error_place_not_found))
            }
        }
        return
    }

    val distanceKm = DistanceCalculator.distanceKm(
        state.userLatitude,
        state.userLongitude,
        place.latitude,
        place.longitude
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ColabBackground),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                PlaceDetailHeader(
                    imageUrls = place.imageUrls,
                    placeTitle = place.title,
                    onNavigateBack = actions.onNavigateBack
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    PlaceDetailInfo(place = place, distanceKm = distanceKm)
                    PlaceDetailActions(
                        place = place,
                        isInterested = state.isInterested,
                        isVisited = state.isVisited,
                        isOwner = state.isOwner,
                        onToggleInterest = actions.onToggleInterest,
                        onToggleVisited = actions.onToggleVisited,
                        onOpenExperience = actions.onOpenExperience,
                        onEditPlace = actions.onEditPlace,
                        onMarkResolved = actions.onMarkResolved,
                        onDeletePlace = actions.onDeleteClick
                    )
                    PlaceAboutAndLocation(place = place)
                    CommentsHeaderAndInput(
                        commentsCount = state.comments.size,
                        commentText = state.commentText,
                        errorMessage = state.commentForm.messageOf(FormField.COMMENT),
                        isSending = state.isSendingComment,
                        requester = requesters[FormField.COMMENT],
                        onCommentTextChange = actions.onCommentTextChange,
                        onCommentFocusLost = actions.onCommentFocusLost,
                        onSendComment = actions.onSendComment
                    )
                    if (state.errorRes != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
                    }
                }
            }

            items(state.comments, key = { it.id }) { comment ->
                CommentCard(comment = comment)
            }
        }

        if (state.isDeleting) {
            LoadingOverlay(dimBackground = true)
        }
    }

    state.experience?.let { experience ->
        VisitExperienceDialog(
            state = experience,
            onRatingChange = actions.onExperienceRatingChange,
            onTextChange = actions.onExperienceTextChange,
            onSubmit = actions.onSubmitExperience,
            onDismiss = actions.onDismissExperience
        )
    }

    if (state.showDeleteDialog) {
        DeletePlaceDialog(onConfirm = actions.onConfirmDelete, onDismiss = actions.onDismissDelete)
    }
}
