package co.edu.uniquindio.apptours.features.user.notifications.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.AppNotification
import co.edu.uniquindio.apptours.domain.model.BadgeId
import co.edu.uniquindio.apptours.domain.model.NotificationArgs
import co.edu.uniquindio.apptours.domain.model.NotificationType
import co.edu.uniquindio.apptours.domain.model.TrophyId
import co.edu.uniquindio.apptours.domain.model.UserLevel
import co.edu.uniquindio.apptours.features.moderator.profile.components.titleRes
import co.edu.uniquindio.apptours.features.user.profile.components.titleRes

/** Título y cuerpo de una notificación, armados en el idioma actual a partir del tipo y sus datos. */
data class NotificationText(val title: String, val body: String)

@Composable
fun AppNotification.text(): NotificationText {
    // Notificaciones creadas antes de IT-11: traen el texto ya escrito.
    if (args.isEmpty() && title.isNotBlank()) return NotificationText(title, body)

    val actor = args[NotificationArgs.ACTOR_NAME].orEmpty()
    val place = args[NotificationArgs.PLACE_TITLE].orEmpty()
    val points = args[NotificationArgs.POINTS]?.toIntOrNull() ?: 0

    return when (type) {
        NotificationType.VOTE -> NotificationText(
            stringResource(R.string.notif_vote_title, actor, place),
            stringResource(R.string.notif_vote_body, points)
        )
        NotificationType.COMMENT -> NotificationText(
            stringResource(R.string.notif_comment_title, actor, place),
            args[NotificationArgs.COMMENT_TEXT].orEmpty()
        )
        NotificationType.VERIFIED -> NotificationText(
            stringResource(R.string.notif_verified_title, place),
            stringResource(R.string.notif_verified_body, points)
        )
        NotificationType.REJECTED -> NotificationText(
            stringResource(R.string.notif_rejected_title, place),
            stringResource(R.string.notif_rejected_body, args[NotificationArgs.REASON].orEmpty())
        )
        NotificationType.RESOLVED -> NotificationText(
            stringResource(R.string.notif_resolved_title, place),
            stringResource(R.string.notif_resolved_body, points)
        )
        NotificationType.LEVEL_UP -> {
            val level = args[NotificationArgs.LEVEL]?.let { runCatching { UserLevel.valueOf(it) }.getOrNull() }
            NotificationText(
                stringResource(R.string.notif_level_title, level?.let { stringResource(it.labelRes()) }.orEmpty()),
                stringResource(R.string.notif_level_body, points)
            )
        }
        NotificationType.BADGE -> {
            val trophy = args[NotificationArgs.TROPHY]?.let { runCatching { TrophyId.valueOf(it) }.getOrNull() }
            val badge = args[NotificationArgs.BADGE]?.let { runCatching { BadgeId.valueOf(it) }.getOrNull() }
            when {
                trophy != null -> NotificationText(
                    stringResource(R.string.notif_trophy_title, stringResource(trophy.titleRes())),
                    stringResource(R.string.notif_trophy_body)
                )
                badge != null -> NotificationText(
                    stringResource(R.string.notif_badge_title, stringResource(badge.titleRes())),
                    stringResource(R.string.notif_badge_body)
                )
                else -> NotificationText(title, body)
            }
        }
    }
}
