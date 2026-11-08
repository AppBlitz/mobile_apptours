package co.edu.uniquindio.apptours.features.moderator.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.ModeratorStats

/** Insights del moderador (M-06): totales, tasa de rechazo y actividad de hoy y de la semana. */
@Composable
internal fun ModeratorStatsDashboard(
    stats: ModeratorStats,
    reviewsToday: Int,
    reviewsThisWeek: Int
) {
    Text(
        text = stringResource(R.string.moderator_stats_title),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatBox(stringResource(R.string.moderator_stats_reviewed), stats.reviewedCount.toString(), Modifier.weight(1f))
        StatBox(stringResource(R.string.moderator_stats_verified), stats.verifiedCount.toString(), Modifier.weight(1f), ColabMediumGreen)
        StatBox(stringResource(R.string.moderator_stats_rejected), stats.rejectedCount.toString(), Modifier.weight(1f), ColabError)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatBox(stringResource(R.string.moderator_stats_resolved), stats.resolvedCount.toString(), Modifier.weight(1f), ColabBrown)
        StatBox(
            stringResource(R.string.moderator_stats_rejection_rate),
            stringResource(R.string.moderator_stats_percent, stats.rejectionRate),
            Modifier.weight(1f)
        )
        StatBox(
            stringResource(R.string.moderator_stats_today_week),
            stringResource(R.string.moderator_stats_today_week_value, reviewsToday, reviewsThisWeek),
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier, valueColor: Color = ColabDarkGreen) {
    Surface(
        modifier = modifier.border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, color = ColabTextSecondary, lineHeight = 15.sp)
        }
    }
}
