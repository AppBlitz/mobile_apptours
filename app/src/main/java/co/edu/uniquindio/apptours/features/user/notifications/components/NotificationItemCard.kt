package co.edu.uniquindio.apptours.features.user.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.relativeTimeLabel
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.NotificationType

/** Tarjeta de notificación: tocarla la abre; el menú ⋮ la marca como leída o no leída (U-14). */
@Composable
internal fun NotificationItemCard(
    notification: AppNotification,
    onClick: () -> Unit,
    onToggleRead: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val text = notification.text()
    val (icon, color) = when (notification.type) {
        NotificationType.VOTE -> Icons.Filled.Favorite to ColabBrown
        NotificationType.COMMENT -> Icons.Filled.ChatBubbleOutline to ColabDarkGreen
        NotificationType.VERIFIED -> Icons.Filled.Verified to ColabMediumGreen
        NotificationType.REJECTED -> Icons.Filled.Close to ColabError
        NotificationType.BADGE -> Icons.Filled.EmojiEvents to ColabBrown
        NotificationType.RESOLVED -> Icons.Filled.TaskAlt to ColabMediumGreen
        NotificationType.LEVEL_UP -> Icons.Filled.MilitaryTech to ColabBrown
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) ColabSurface else ColabCardSurface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text.title,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = text.body, fontSize = 13.sp, color = ColabTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ColabBrown)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = relativeTimeLabel(notification.createdAt),
                        fontSize = 11.sp,
                        color = ColabTextSecondary
                    )
                }
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.notifications_options),
                        tint = ColabTextSecondary
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    if (notification.isRead) R.string.notifications_mark_unread
                                    else R.string.notifications_mark_read
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (notification.isRead) Icons.Outlined.MarkEmailUnread else Icons.Outlined.MarkEmailRead,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onToggleRead()
                        }
                    )
                }
            }
        }
    }
}
