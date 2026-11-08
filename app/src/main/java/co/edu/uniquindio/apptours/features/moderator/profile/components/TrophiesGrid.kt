package co.edu.uniquindio.apptours.features.moderator.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.DateFormatter
import co.edu.uniquindio.apptours.domain.model.Trophy
import co.edu.uniquindio.apptours.domain.model.TrophyId

/** Trofeos reales del moderador con progreso y fecha de obtención. */
@Composable
internal fun TrophiesGrid(
    trophies: List<Trophy>,
    selected: TrophyId?,
    onSelect: (TrophyId) -> Unit
) {
    val current = trophies.find { it.id == selected } ?: trophies.firstOrNull() ?: return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.trophies_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = stringResource(R.string.badges_count, trophies.count { it.unlocked }, trophies.size),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    trophies.chunked(3).forEach { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { trophy ->
                TrophyTile(
                    trophy = trophy,
                    selected = trophy.id == current.id,
                    onClick = { onSelect(trophy.id) },
                    modifier = Modifier.weight(1f)
                )
            }
            repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabSageBg.copy(alpha = 0.65f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(current.id.titleRes()),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(current.id.descriptionRes(), current.id.target),
                fontSize = 13.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val earnedAt = current.earnedAt
            Text(
                text = when {
                    earnedAt != null -> stringResource(R.string.badges_earned_on, DateFormatter.longDate(earnedAt))
                    else -> stringResource(R.string.badges_progress_detail, current.progress, current.id.target)
                },
                fontSize = 11.sp,
                color = ColabTextSecondary
            )
        }
    }
}

@Composable
private fun TrophyTile(trophy: Trophy, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (trophy.unlocked) ColabSurface else ColabCardSurface,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) ColabDarkGreen else ColabGrayBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = CircleShape,
                color = if (trophy.unlocked) ColabAmberBg else ColabGrayBorder
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (trophy.unlocked) trophy.id.icon() else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = if (trophy.unlocked) ColabBrown else ColabTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(trophy.id.titleRes()),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColabTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (!trophy.unlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { trophy.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ColabMediumGreen,
                    trackColor = ColabGrayBorder
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.badges_progress, trophy.progress, trophy.id.target),
                    fontSize = 10.sp,
                    color = ColabTextSecondary
                )
            }
        }
    }
}
