package co.edu.uniquindio.apptours.features.user.myplaces

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
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
import co.edu.uniquindio.apptours.features.user.myplaces.components.MyPlaceCard

@Composable
fun MyPlacesRoute(
    onNavigateBack: () -> Unit,
    onOpenPlace: (String) -> Unit,
    viewModel: MyPlacesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MyPlacesScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onSelectTab = viewModel::onSelectTab,
        onOpenPlace = onOpenPlace,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun MyPlacesScreen(
    state: MyPlacesUiState,
    onNavigateBack: () -> Unit,
    onSelectTab: (MyPlacesTab) -> Unit,
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
                text = stringResource(R.string.my_places_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
        }

        ScrollableTabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = ColabBackground,
            contentColor = ColabDarkGreen,
            edgePadding = 16.dp
        ) {
            MyPlacesTab.entries.forEach { tab ->
                Tab(
                    selected = tab == state.selectedTab,
                    onClick = { onSelectTab(tab) },
                    text = {
                        Text(
                            text = stringResource(R.string.my_places_tab_format, stringResource(tab.labelRes()), state.counts[tab] ?: 0),
                            fontWeight = if (tab == state.selectedTab) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = ColabDarkGreen,
                    unselectedContentColor = ColabTextSecondary
                )
            }
        }

        when {
            state.loadErrorRes != null -> ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry)
            state.isLoading -> LoadingOverlay()
            state.places.isEmpty() -> EmptyTab(tab = state.selectedTab)
            else -> LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.places, key = { it.id }) { place ->
                    MyPlaceCard(place = place, onClick = { onOpenPlace(place.id) })
                }
            }
        }
    }
}

@Composable
private fun EmptyTab(tab: MyPlacesTab) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            tint = ColabTextSecondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(tab.emptyRes()),
            color = ColabTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun MyPlacesTab.labelRes(): Int = when (this) {
    MyPlacesTab.ACTIVAS -> R.string.my_places_tab_active
    MyPlacesTab.PENDIENTES -> R.string.my_places_tab_pending
    MyPlacesTab.FINALIZADAS -> R.string.my_places_tab_resolved
    MyPlacesTab.RECHAZADAS -> R.string.my_places_tab_rejected
}

private fun MyPlacesTab.emptyRes(): Int = when (this) {
    MyPlacesTab.ACTIVAS -> R.string.my_places_empty_active
    MyPlacesTab.PENDIENTES -> R.string.my_places_empty_pending
    MyPlacesTab.FINALIZADAS -> R.string.my_places_empty_resolved
    MyPlacesTab.RECHAZADAS -> R.string.my_places_empty_rejected
}
