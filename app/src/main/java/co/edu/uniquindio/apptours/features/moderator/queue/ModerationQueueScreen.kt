package co.edu.uniquindio.apptours.features.moderator.queue

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*
import co.edu.uniquindio.apptours.features.moderator.queue.components.*

@Composable
fun ModerationQueueRoute(
    requestedFilter: ModerationFilter,
    onOpenReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModerationQueueViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // La pestaña inferior (Por revisar / Historial) fija el filtro inicial.
    LaunchedEffect(requestedFilter) { viewModel.onFilterSelected(requestedFilter) }

    ModerationQueueScreen(
        state = state,
        onFilterSelected = viewModel::onFilterSelected,
        onOpenReview = onOpenReview,
        modifier = modifier
    )
}

@Composable
fun ModerationQueueScreen(
    state: ModerationQueueUiState,
    onFilterSelected: (ModerationFilter) -> Unit,
    onOpenReview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ModerationPanelHeader(stats = state.stats)

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeratorFilterChip(
                    text = "Pendientes ${state.stats.pendingCount}",
                    selected = state.filter == ModerationFilter.PENDIENTES,
                    onClick = { onFilterSelected(ModerationFilter.PENDIENTES) }
                )
                ModeratorFilterChip(
                    text = "Verificadas",
                    selected = state.filter == ModerationFilter.VERIFICADAS,
                    onClick = { onFilterSelected(ModerationFilter.VERIFICADAS) }
                )
                ModeratorFilterChip(
                    text = "Rechazadas",
                    selected = state.filter == ModerationFilter.RECHAZADAS,
                    onClick = { onFilterSelected(ModerationFilter.RECHAZADAS) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cola de revisión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = ColabDarkGreen
                )
                Text(
                    text = "Más antiguas primero",
                    fontSize = 12.sp,
                    color = ColabTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(state.places, key = { it.id }) { place ->
                    ModeratorQueueCard(
                        place = place,
                        onReviewClick = { onOpenReview(place.id) }
                    )
                }
            }
        }
    }
}
