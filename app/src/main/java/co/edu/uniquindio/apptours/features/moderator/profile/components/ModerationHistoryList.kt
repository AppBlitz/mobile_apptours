package co.edu.uniquindio.apptours.features.moderator.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.relativeTimeLabel
import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry

/** Últimas acciones del moderador, tomadas del historial permanente (`moderationLog`). */
@Composable
internal fun ModerationHistoryList(
    entries: List<ModerationLogEntry>,
    onOpenPlace: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.moderator_recent_title),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(10.dp))
    if (entries.isEmpty()) {
        Text(
            text = stringResource(R.string.moderator_recent_empty),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEach { entry ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPlace(entry.placeId) },
                shape = RoundedCornerShape(14.dp),
                color = ColabSurface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = entry.action.icon(),
                        contentDescription = null,
                        tint = entry.action.color(),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.placeTitle,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ColabTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(entry.action.labelRes()) + " · " + relativeTimeLabel(entry.createdAt),
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                        entry.reason?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = stringResource(R.string.moderator_recent_reason, it),
                                fontSize = 12.sp,
                                color = ColabError,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.xp_value, entry.xpAwarded),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ColabBrown
                    )
                }
            }
        }
    }
}
