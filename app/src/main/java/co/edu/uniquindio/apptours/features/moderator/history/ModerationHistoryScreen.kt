package co.edu.uniquindio.apptours.features.moderator.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ErrorMessage
import co.edu.uniquindio.apptours.core.designsystem.components.LoadingOverlay
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.features.moderator.history.components.ModerationHistoryCard

@Composable
fun ModerationHistoryRoute(
    onOpenPlace: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModerationHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ModerationHistoryScreen(
        state = state,
        onOpenPlace = onOpenPlace,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@Composable
fun ModerationHistoryScreen(
    state: ModerationHistoryUiState,
    onOpenPlace: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = modifier.background(ColabBackground))
        return
    }
    if (state.isLoading) {
        LoadingOverlay(modifier = modifier.background(ColabBackground))
        return
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColabBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.moderation_history_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = stringResource(R.string.moderation_history_summary, state.approvedCount, state.rejectedCount),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (state.entries.isEmpty()) {
            EmptyHistory()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(state.entries, key = { it.id }) { place ->
                    ModerationHistoryCard(place = place, onClick = { onOpenPlace(place.id) })
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = ColabTextSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.moderation_history_empty),
            color = ColabTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
