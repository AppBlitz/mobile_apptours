package co.edu.uniquindio.apptours.features.user.notifications

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
import co.edu.uniquindio.apptours.features.user.notifications.components.NotificationItemCard

@Composable
fun NotificationsRoute(
    onOpenPlaceDetail: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    NotificationsScreen(
        state = state,
        onFilterSelected = viewModel::onFilterSelected,
        onNotificationClick = { notification ->
            viewModel.onNotificationClick(notification)
            notification.relatedPlaceId?.let(onOpenPlaceDetail)
        },
        onMarkAllRead = viewModel::onMarkAllRead,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    onFilterSelected: (NotificationFilter) -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit,
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
    val unreadCount = state.unreadCount
    val todayList = state.notifications.filter { DateFormatter.isToday(it.createdAt) }
    val earlierList = state.notifications.filter { !DateFormatter.isToday(it.createdAt) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .padding(20.dp)
    ) {
        // Cabecera de Notificaciones (Página 12 del PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notificaciones",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                Text(
                    text = "$unreadCount sin leer",
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
            }
            TextButton(onClick = { onMarkAllRead() }) {
                Text(
                    text = "Marcar todas como\nleídas",
                    color = ColabDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryFilterChip(
                text = "Todas",
                selected = state.filter == NotificationFilter.TODAS,
                onClick = { onFilterSelected(NotificationFilter.TODAS) }
            )
            CategoryFilterChip(
                text = "No leídas · $unreadCount",
                selected = state.filter == NotificationFilter.NO_LEIDAS,
                onClick = { onFilterSelected(NotificationFilter.NO_LEIDAS) }
            )
            CategoryFilterChip(
                text = "Logros",
                selected = state.filter == NotificationFilter.LOGROS,
                onClick = { onFilterSelected(NotificationFilter.LOGROS) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (todayList.isNotEmpty()) {
                item {
                    Text(
                        text = "HOY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabTextSecondary
                    )
                }
                items(todayList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            onNotificationClick(notif)
                        }
                    )
                }
            }

            if (earlierList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ANTERIORES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabTextSecondary
                    )
                }
                items(earlierList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            onNotificationClick(notif)
                        }
                    )
                }
            }
        }
    }
}
