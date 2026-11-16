package co.edu.uniquindio.apptours.features.user.profile.components

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
import co.edu.uniquindio.apptours.domain.model.Badge
import co.edu.uniquindio.apptours.domain.model.BadgeId

/** Insignias reales (U-20): progreso calculado con datos de la nube y fecha real de obtención. */
@Composable
internal fun BadgesSection(
    badges: List<Badge>,
    selectedBadge: Badge,
    onSelectBadge: (BadgeId) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.badges_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = stringResource(R.string.badges_count, badges.count { it.unlocked }, badges.size),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    badges.chunked(3).forEach { rowBadges ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rowBadges.forEach { badge ->
                BadgeTile(
                    badge = badge,
                    selected = badge.id == selectedBadge.id,
                    onClick = { onSelectBadge(badge.id) },
                    modifier = Modifier.weight(1f)
                )
            }
            // Relleno para que la última fila no estire sus tarjetas.
            repeat(3 - rowBadges.size) { Spacer(modifier = Modifier.weight(1f)) }
        }
    }

    BadgeDetail(badge = selectedBadge)
}

@Composable
private fun BadgeTile(badge: Badge, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (badge.unlocked) ColabSurface else ColabCardSurface,
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
                color = if (badge.unlocked) ColabAmberBg else ColabGrayBorder
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (badge.unlocked) badge.id.icon() else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = if (badge.unlocked) ColabBrown else ColabTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(badge.id.titleRes()),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColabTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (!badge.unlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { badge.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ColabMediumGreen,
                    trackColor = ColabGrayBorder
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.badges_progress, badge.progress, badge.target),
                    fontSize = 10.sp,
                    color = ColabTextSecondary
                )
            }
        }
    }
}

@Composable
private fun BadgeDetail(badge: Badge) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabSageBg.copy(alpha = 0.65f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(badge.id.titleRes()),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(badge.id.descriptionRes(), badge.target),
                fontSize = 13.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val earnedAt = badge.earnedAt
            Text(
                text = when {
                    earnedAt != null -> stringResource(R.string.badges_earned_on, DateFormatter.longDate(earnedAt))
                    badge.unlocked -> stringResource(R.string.badges_just_earned)
                    else -> stringResource(R.string.badges_progress_detail, badge.progress, badge.target)
                },
                fontSize = 11.sp,
                color = ColabTextSecondary
            )
        }
    }
}
