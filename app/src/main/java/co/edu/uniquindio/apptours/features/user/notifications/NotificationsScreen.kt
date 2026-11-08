package co.edu.uniquindio.apptours.features.user.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryFilterChip
import co.edu.uniquindio.apptours.core.designsystem.components.ErrorMessage
import co.edu.uniquindio.apptours.core.designsystem.components.LoadingOverlay
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.DateFormatter
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.features.user.notifications.components.NotificationItemCard

/**
 * Lista de notificaciones (U-13, U-14). La usan el usuario y el moderador: tocar una la marca
 * como leída y, si tiene publicación asociada, abre [onOpenPlaceDetail].
 */
@Composable
fun NotificationsRoute(
    onOpenPlaceDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
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
        onToggleRead = viewModel::onToggleRead,
        onMarkAllRead = viewModel::onMarkAllRead,
        onRetry = viewModel::onRetry,
        modifier = modifier
    )
}

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    onFilterSelected: (NotificationFilter) -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onToggleRead: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit,
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
    val unreadCount = state.unreadCount
    val todayList = state.notifications.filter { DateFormatter.isToday(it.createdAt) }
    val earlierList = state.notifications.filter { !DateFormatter.isToday(it.createdAt) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColabBackground)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.notifications_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                Text(
                    text = pluralStringResource(R.plurals.notifications_unread, unreadCount, unreadCount),
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
            }
            TextButton(onClick = onMarkAllRead, enabled = unreadCount > 0) {
                Text(
                    text = stringResource(R.string.notifications_mark_all_read),
                    color = if (unreadCount > 0) ColabDarkGreen else ColabTextSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilterChip(
                text = stringResource(R.string.notifications_filter_all),
                selected = state.filter == NotificationFilter.TODAS,
                onClick = { onFilterSelected(NotificationFilter.TODAS) }
            )
            CategoryFilterChip(
                text = stringResource(R.string.notifications_filter_unread, unreadCount),
                selected = state.filter == NotificationFilter.NO_LEIDAS,
                onClick = { onFilterSelected(NotificationFilter.NO_LEIDAS) }
            )
            CategoryFilterChip(
                text = stringResource(R.string.notifications_filter_achievements),
                selected = state.filter == NotificationFilter.LOGROS,
                onClick = { onFilterSelected(NotificationFilter.LOGROS) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.notifications.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    tint = ColabTextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.notifications_empty),
                    color = ColabTextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (todayList.isNotEmpty()) {
                item { SectionLabel(stringResource(R.string.notifications_today)) }
                items(todayList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = { onNotificationClick(notif) },
                        onToggleRead = { onToggleRead(notif) }
                    )
                }
            }
            if (earlierList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    SectionLabel(stringResource(R.string.notifications_earlier))
                }
                items(earlierList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = { onNotificationClick(notif) },
                        onToggleRead = { onToggleRead(notif) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColabTextSecondary)
}
