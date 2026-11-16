package co.edu.uniquindio.apptours.features.moderator.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.core.designsystem.components.ErrorMessage
import co.edu.uniquindio.apptours.core.designsystem.components.LoadingOverlay
import co.edu.uniquindio.apptours.core.theme.ColabBackground
import co.edu.uniquindio.apptours.domain.model.TrophyId
import co.edu.uniquindio.apptours.features.moderator.profile.components.*
import co.edu.uniquindio.apptours.features.user.account.components.LanguageSelector

/** Perfil del moderador (M-06): insights reales, XP con rango, trofeos y últimas acciones. */
@Composable
fun ModeratorProfileRoute(
    onOpenPlace: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModeratorProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ModeratorProfileEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    ModeratorProfileScreen(
        state = state,
        onOpenPlace = onOpenPlace,
        onSelectTrophy = viewModel::onSelectTrophy,
        onLogout = viewModel::onLogout,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@Composable
fun ModeratorProfileScreen(
    state: ModeratorProfileUiState,
    onOpenPlace: (String) -> Unit,
    onSelectTrophy: (TrophyId) -> Unit,
    onLogout: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = modifier.background(ColabBackground))
        return
    }
    val profile = state.profile
    if (state.isLoading || profile == null) {
        LoadingOverlay(modifier = modifier.background(ColabBackground))
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
    ) {
        ModeratorXpHeader(
            fullName = profile.user?.fullName.orEmpty(),
            email = profile.user?.email.orEmpty(),
            xp = profile.stats.xp,
            rank = profile.rank,
            onLogout = onLogout
        )
        Column(modifier = Modifier.padding(20.dp)) {
            ModeratorStatsDashboard(
                stats = profile.stats,
                reviewsToday = profile.reviewsToday,
                reviewsThisWeek = profile.reviewsThisWeek
            )
            Spacer(modifier = Modifier.height(22.dp))
            TrophiesGrid(trophies = profile.trophies, selected = state.selectedTrophy, onSelect = onSelectTrophy)
            Spacer(modifier = Modifier.height(20.dp))
            ModeratorXpRulesCard()
            Spacer(modifier = Modifier.height(20.dp))
            ModerationHistoryList(entries = profile.recentActions, onOpenPlace = onOpenPlace)
            Spacer(modifier = Modifier.height(24.dp))
            LanguageSelector()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
