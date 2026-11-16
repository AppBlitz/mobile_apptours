package co.edu.uniquindio.apptours.features.user.recommendations.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.AsyncPlaceImage
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryBadgeChip
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.Formatters
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.Recommendation
import co.edu.uniquindio.apptours.domain.model.RecommendationReason
import kotlin.math.roundToInt

/** Tarjeta de un lugar recomendado con la justificación y el porcentaje de coincidencia. */
@Composable
internal fun RecommendationCard(recommendation: Recommendation, onClick: () -> Unit) {
    val place = recommendation.place
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row {
                AsyncPlaceImage(
                    url = place.coverImageUrl,
                    contentDescription = place.title,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadgeChip(place.category)
                        Text(
                            text = stringResource(
                                R.string.recommendation_distance,
                                Formatters.formatDistance(recommendation.distanceKm)
                            ),
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = place.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = ColabBrown,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reasonText(recommendation.reason),
                    fontSize = 13.sp,
                    color = ColabDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { recommendation.score.toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.weight(1f),
                    color = ColabMediumGreen,
                    trackColor = ColabSageBg
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.recommendation_match, (recommendation.score * 100).roundToInt()),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColabTextSecondary
                )
            }
        }
    }
}

/** "Porque visitaste 4 lugares de Gastronomía · a 1,2 km de una zona que frecuentas". */
@Composable
private fun reasonText(reason: RecommendationReason): String = when (reason) {
    is RecommendationReason.Popular ->
        pluralStringResource(R.plurals.recommendation_reason_popular, reason.votes, reason.votes)

    is RecommendationReason.History -> {
        val categoryPart = if (reason.visitsInCategory > 0) {
            pluralStringResource(
                R.plurals.recommendation_reason_category,
                reason.visitsInCategory,
                reason.visitsInCategory,
                stringResource(reason.category.labelRes())
            )
        } else {
            stringResource(R.string.recommendation_reason_new_category, stringResource(reason.category.labelRes()))
        }
        val zoneDistance = reason.distanceToZoneKm
        if (zoneDistance == null) {
            categoryPart
        } else {
            stringResource(
                R.string.recommendation_reason_with_zone,
                categoryPart,
                Formatters.formatDistance(zoneDistance)
            )
        }
    }
}
