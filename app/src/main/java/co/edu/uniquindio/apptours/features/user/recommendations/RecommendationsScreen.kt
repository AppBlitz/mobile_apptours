package co.edu.uniquindio.apptours.features.user.recommendations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import co.edu.uniquindio.apptours.core.map.DeviceLocationResult
import co.edu.uniquindio.apptours.core.map.rememberDeviceLocationRequester
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.features.user.recommendations.components.RecommendationCard
import co.edu.uniquindio.apptours.features.user.recommendations.components.TasteSummary

@Composable
fun RecommendationsRoute(
    onNavigateBack: () -> Unit,
    onOpenPlace: (String) -> Unit,
    viewModel: RecommendationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // La cercanía se mide desde el teléfono si el usuario lo permite; si no, desde su dirección guardada.
    val requestDeviceLocation = rememberDeviceLocationRequester { result ->
        if (result is DeviceLocationResult.Found) viewModel.onDeviceLocation(result.latitude, result.longitude)
    }
    LaunchedEffect(Unit) { requestDeviceLocation() }

    RecommendationsScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onOpenPlace = onOpenPlace,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun RecommendationsScreen(
    state: RecommendationsUiState,
    onNavigateBack: () -> Unit,
    onOpenPlace: (String) -> Unit,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = ColabDarkGreen
                )
            }
            Text(
                text = stringResource(R.string.recommendations_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
        }

        val result = state.result
        when {
            state.loadErrorRes != null -> ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry)
            state.isLoading || result == null -> LoadingOverlay()
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TasteSummary(result = result, usingDeviceLocation = state.usingDeviceLocation)
                }
                if (result.recommendations.isEmpty()) {
                    item { EmptyRecommendations() }
                } else {
                    items(result.recommendations, key = { it.place.id }) { recommendation ->
                        RecommendationCard(
                            recommendation = recommendation,
                            onClick = { onOpenPlace(recommendation.place.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecommendations() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            tint = ColabTextSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.recommendations_empty),
            color = ColabTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
