package co.edu.uniquindio.apptours.features.moderator.history.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryBadgeChip
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.relativeTimeLabel
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

private data class Decision(val labelRes: Int, val icon: ImageVector, val color: Color, val background: Color)

private fun Place.decision(): Decision = when (status) {
    PlaceStatus.RECHAZADA -> Decision(R.string.moderation_decision_rejected, Icons.Filled.Cancel, ColabError, ColabErrorBg)
    PlaceStatus.RESUELTA -> Decision(R.string.moderation_decision_resolved, Icons.Filled.TaskAlt, ColabBrown, ColabAmberBg)
    else -> Decision(R.string.moderation_decision_approved, Icons.Filled.CheckCircle, ColabMediumGreen, ColabSageBg)
}

/** Tarjeta de una decisión del historial: qué se revisó, qué se decidió, cuándo y (si aplica) por qué. */
@Composable
internal fun ModerationHistoryCard(
    place: Place,
    onClick: () -> Unit
) {
    val decision = place.decision()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(12.dp), color = decision.background) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = decision.icon,
                            contentDescription = null,
                            tint = decision.color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(decision.labelRes),
                            color = decision.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                place.reviewedAt?.let {
                    Text(
                        text = stringResource(R.string.moderation_reviewed_at, relativeTimeLabel(it)),
                        fontSize = 12.sp,
                        color = ColabTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = place.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryBadgeChip(category = place.category)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.moderation_author, place.authorName),
                    fontSize = 12.sp,
                    color = ColabTextSecondary
                )
            }

            val reason = place.rejectionReason
            if (place.status == PlaceStatus.RECHAZADA && !reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ColabErrorBg
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.moderation_rejection_reason),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColabError
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reason,
                            fontSize = 13.sp,
                            color = ColabTextPrimary
                        )
                    }
                }
            }
        }
    }
}
