package co.edu.uniquindio.apptours.features.moderator.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.core.util.messageOf
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.features.moderator.review.components.*
import co.edu.uniquindio.apptours.features.user.placedetail.components.CommentCard

@Composable
fun ModeratorReviewRoute(
    onNavigateBack: () -> Unit,
    viewModel: ModeratorReviewViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ModeratorReviewEvent.Done -> onNavigateBack()
                is ModeratorReviewEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    ModeratorReviewScreen(
        state = state,
        requesters = requesters,
        actions = ModeratorReviewActions(
            onNavigateBack = onNavigateBack,
            onToggleCheck = viewModel::onToggleCheck,
            onVerify = viewModel::onVerify,
            onMarkResolved = viewModel::onMarkResolved,
            onOpenReject = viewModel::onOpenReject,
            onDismissReject = viewModel::onDismissReject,
            onReasonChange = viewModel::onReasonChange,
            onQuickReason = viewModel::onQuickReason,
            onReasonFocusLost = viewModel::onReasonFocusLost,
            onConfirmReject = viewModel::onConfirmReject,
            onRetry = viewModel::onRetry
        )
    )
}

data class ModeratorReviewActions(
    val onNavigateBack: () -> Unit,
    val onToggleCheck: (ChecklistItem) -> Unit,
    val onVerify: () -> Unit,
    val onMarkResolved: () -> Unit,
    val onOpenReject: () -> Unit,
    val onDismissReject: () -> Unit,
    val onReasonChange: (String) -> Unit,
    val onQuickReason: (String) -> Unit,
    val onReasonFocusLost: () -> Unit,
    val onConfirmReject: () -> Unit,
    val onRetry: () -> Unit
)

@Composable
fun ModeratorReviewScreen(
    state: ModeratorReviewUiState,
    requesters: FieldRequesters,
    actions: ModeratorReviewActions
) {
    val place = state.place
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = actions.onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (place == null) {
        if (state.isLoading) {
            LoadingOverlay(modifier = Modifier.background(ColabBackground))
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.error_place_not_found))
            }
        }
        return
    }

    // Qué acciones tienen sentido según el estado actual (M-02, M-03, M-04).
    val canBeVerified = place.status == PlaceStatus.PENDIENTE || place.status == PlaceStatus.RECHAZADA
    val canBeResolved = place.status != PlaceStatus.RESUELTA
    val canBeRejected = place.status != PlaceStatus.RECHAZADA

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColabBackground)
                .verticalScroll(rememberScrollState())
        ) {
            ReviewPlaceHeader(onNavigateBack = actions.onNavigateBack)

            Column(modifier = Modifier.padding(20.dp)) {
                ReviewPlaceInfo(place = place)

                Spacer(modifier = Modifier.height(14.dp))
                CurrentStatusBanner(place = place)

                if (canBeVerified) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.review_checklist_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = ColabDarkGreen
                    )
                    Text(
                        text = stringResource(R.string.review_checklist_hint),
                        fontSize = 12.sp,
                        color = ColabTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ChecklistItem.entries.forEach { item ->
                        ChecklistItemRow(
                            text = stringResource(item.labelRes),
                            checked = item in state.checkedItems,
                            onCheckedChange = { actions.onToggleCheck(item) },
                            enabled = !state.isProcessing
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.review_comments_title, state.comments.size),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ColabDarkGreen
                )
                if (state.comments.isEmpty()) {
                    Text(
                        text = stringResource(R.string.review_no_comments),
                        fontSize = 13.sp,
                        color = ColabTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            state.comments.forEach { comment -> CommentCard(comment = comment) }

            Column(modifier = Modifier.padding(20.dp)) {
                if (canBeVerified) {
                    ColabPrimaryButton(
                        text = stringResource(R.string.review_verify),
                        leadingIcon = Icons.Filled.Check,
                        enabled = state.canVerify,
                        onClick = actions.onVerify
                    )
                    if (!state.canVerify && !state.isProcessing) {
                        val missing = ChecklistItem.entries.size - state.checkedItems.size
                        Text(
                            text = pluralStringResource(R.plurals.review_verify_missing, missing, missing),
                            fontSize = 12.sp,
                            color = ColabTextSecondary,
                            modifier = Modifier.padding(top = 4.dp, start = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (canBeResolved) {
                        ColabOutlinedButton(
                            text = stringResource(R.string.review_resolve),
                            leadingIcon = Icons.Outlined.TaskAlt,
                            onClick = actions.onMarkResolved,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (canBeRejected) {
                        ColabOutlinedButton(
                            text = stringResource(R.string.review_reject),
                            borderColor = ColabError,
                            contentColor = ColabError,
                            leadingIcon = Icons.Filled.Close,
                            onClick = actions.onOpenReject,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (state.errorRes != null && !state.showRejectSheet) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        if (state.isProcessing && !state.showRejectSheet) {
            LoadingOverlay(dimBackground = true)
        }
    }

    if (state.showRejectSheet) {
        RejectReasonSheet(
            place = place,
            reason = state.rejectionReason,
            reasonError = state.rejectForm.messageOf(FormField.REJECTION_REASON),
            generalError = state.errorRes?.let { stringResource(it) },
            enabled = !state.isProcessing,
            requester = requesters[FormField.REJECTION_REASON],
            onReasonChange = actions.onReasonChange,
            onQuickReason = actions.onQuickReason,
            onReasonFocusLost = actions.onReasonFocusLost,
            onDismiss = actions.onDismissReject,
            onConfirm = actions.onConfirmReject
        )
    }
}

/** Estado actual de la publicación y, si fue rechazada, el motivo que ya se le dio al autor. */
@Composable
private fun CurrentStatusBanner(place: Place) {
    val (color, background) = when (place.status) {
        PlaceStatus.PENDIENTE -> ColabBrown to ColabAmberBg
        PlaceStatus.VERIFICADA -> ColabMediumGreen to ColabSageBg
        PlaceStatus.RECHAZADA -> ColabError to ColabErrorBg
        PlaceStatus.RESUELTA -> ColabDarkGreen to ColabCardSurface
    }
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = background) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.review_current_status, stringResource(place.status.labelRes())),
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            val reason = place.rejectionReason
            if (place.status == PlaceStatus.RECHAZADA && !reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.review_previous_reason, reason),
                    color = ColabTextPrimary,
                    fontSize = 13.sp
                )
            }
        }
    }
}
