package co.edu.uniquindio.apptours.features.moderator.home

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
import co.edu.uniquindio.apptours.features.moderator.home.components.ModeratorAccountContent
import co.edu.uniquindio.apptours.features.moderator.history.ModerationHistoryRoute
import co.edu.uniquindio.apptours.features.moderator.queue.ModerationQueueRoute

@Composable
fun ModeratorHomeRoute(
    onOpenReview: (String) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ModeratorHomeViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf(ModeratorBottomTab.POR_REVISAR) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ModeratorHomeEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    Scaffold(
        containerColor = ColabBackground,
        bottomBar = {
            NavigationBar(containerColor = ColabSurface) {
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.POR_REVISAR,
                    onClick = { currentTab = ModeratorBottomTab.POR_REVISAR },
                    icon = { Icon(Icons.Outlined.AssignmentLate, contentDescription = null) },
                    label = { Text("Por revisar") }
                )
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.HISTORIAL,
                    onClick = { currentTab = ModeratorBottomTab.HISTORIAL },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                    label = { Text("Historial") }
                )
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.CUENTA,
                    onClick = { currentTab = ModeratorBottomTab.CUENTA },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = { Text("Cuenta") }
                )
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            ModeratorBottomTab.CUENTA -> ModeratorAccountContent(
                fullName = currentUser?.fullName.orEmpty(),
                email = currentUser?.email.orEmpty(),
                onLogout = viewModel::onLogout,
                modifier = Modifier.padding(innerPadding)
            )
            ModeratorBottomTab.HISTORIAL -> ModerationHistoryRoute(
                onOpenPlace = onOpenReview,
                modifier = Modifier.padding(innerPadding)
            )
            ModeratorBottomTab.POR_REVISAR -> ModerationQueueRoute(
                requestedFilter = ModerationFilter.PENDIENTES,
                onOpenReview = onOpenReview,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
