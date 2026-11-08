package co.edu.uniquindio.apptours.features.moderator.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentLate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.ModerationFilter
import co.edu.uniquindio.apptours.features.moderator.history.ModerationHistoryRoute
import co.edu.uniquindio.apptours.features.moderator.profile.ModeratorProfileRoute
import co.edu.uniquindio.apptours.features.moderator.queue.ModerationQueueRoute
import co.edu.uniquindio.apptours.features.user.notifications.NotificationsRoute

@Composable
fun ModeratorHomeRoute(
    onOpenReview: (String) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ModeratorHomeViewModel = hiltViewModel()
) {
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf(ModeratorBottomTab.POR_REVISAR) }

    Scaffold(
        containerColor = ColabBackground,
        bottomBar = {
            NavigationBar(containerColor = ColabSurface) {
                ModeratorBottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == ModeratorBottomTab.ALERTAS && unreadCount > 0) {
                                BadgedBox(badge = { Badge { Text(unreadCount.toString()) } }) {
                                    Icon(tab.icon(), contentDescription = null)
                                }
                            } else {
                                Icon(tab.icon(), contentDescription = null)
                            }
                        },
                        label = { Text(stringResource(tab.labelRes())) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentTab) {
            ModeratorBottomTab.POR_REVISAR -> ModerationQueueRoute(
                requestedFilter = ModerationFilter.PENDIENTES,
                onOpenReview = onOpenReview,
                modifier = modifier
            )
            ModeratorBottomTab.HISTORIAL -> ModerationHistoryRoute(
                onOpenPlace = onOpenReview,
                modifier = modifier
            )
            ModeratorBottomTab.ALERTAS -> NotificationsRoute(
                onOpenPlaceDetail = onOpenReview,
                modifier = modifier
            )
            ModeratorBottomTab.PERFIL -> ModeratorProfileRoute(
                onOpenPlace = onOpenReview,
                onLoggedOut = onLoggedOut,
                modifier = modifier
            )
        }
    }
}

private fun ModeratorBottomTab.icon(): ImageVector = when (this) {
    ModeratorBottomTab.POR_REVISAR -> Icons.Outlined.AssignmentLate
    ModeratorBottomTab.HISTORIAL -> Icons.Outlined.History
    ModeratorBottomTab.ALERTAS -> Icons.Outlined.Notifications
    ModeratorBottomTab.PERFIL -> Icons.Outlined.Person
}

private fun ModeratorBottomTab.labelRes(): Int = when (this) {
    ModeratorBottomTab.POR_REVISAR -> R.string.moderator_tab_queue
    ModeratorBottomTab.HISTORIAL -> R.string.moderator_tab_history
    ModeratorBottomTab.ALERTAS -> R.string.moderator_tab_alerts
    ModeratorBottomTab.PERFIL -> R.string.moderator_tab_profile
}
