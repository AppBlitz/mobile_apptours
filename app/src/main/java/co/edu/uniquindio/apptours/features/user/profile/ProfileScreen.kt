package co.edu.uniquindio.apptours.features.user.profile

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
import co.edu.uniquindio.apptours.features.user.profile.components.*

@Composable
fun ProfileRoute(
    onOpenMyPlaces: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    ProfileScreen(
        state = state,
        onOpenMyPlaces = onOpenMyPlaces,
        onOpenAccountSettings = onOpenAccountSettings,
        onLogout = viewModel::onLogout,
        onSelectBadge = viewModel::onSelectBadge,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onOpenMyPlaces: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onLogout: () -> Unit,
    onSelectBadge: (BadgeId) -> Unit,
    onRetry: () -> Unit
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (state.isLoading) {
        LoadingOverlay(modifier = Modifier.background(ColabBackground))
        return
    }
    val user = state.user
    val selectedBadge = state.selectedBadge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(
            fullName = user?.fullName.orEmpty(),
            initials = user?.initials.orEmpty(),
            points = user?.points ?: 0,
            levelProgress = state.levelProgress,
            onOpenAccountSettings = onOpenAccountSettings,
            onLogout = onLogout
        )

        Column(modifier = Modifier.padding(20.dp)) {
            StatsDashboard(stats = state.stats)

            Spacer(modifier = Modifier.height(12.dp))

            ColabPrimaryButton(
                text = stringResource(R.string.my_places_title),
                leadingIcon = Icons.Outlined.Inventory2,
                onClick = onOpenMyPlaces
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (selectedBadge != null) {
                BadgesSection(
                    badges = state.badges,
                    selectedBadge = selectedBadge,
                    onSelectBadge = onSelectBadge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PointRulesCard()

            Spacer(modifier = Modifier.height(18.dp))

            ColabOutlinedButton(
                text = "Ajustes de cuenta",
                leadingIcon = Icons.Outlined.Settings,
                onClick = onOpenAccountSettings
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
