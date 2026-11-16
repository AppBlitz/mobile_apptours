package co.edu.uniquindio.apptours.features.user.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.PointAction

/** Tabla de puntos: sale de [PointAction], la misma tabla que usa AwardPointsUseCase al otorgarlos. */
@Composable
internal fun PointRulesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface,
        border = BorderStroke(1.dp, ColabGrayBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.points_rules_title),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            PointAction.entries.forEach { action ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(action.labelRes()), fontSize = 13.sp, color = ColabTextPrimary)
                    Text(
                        text = stringResource(R.string.points_value, action.points),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabDarkGreen
                    )
                }
            }
        }
    }
}

private fun PointAction.labelRes(): Int = when (this) {
    PointAction.CREATE_PLACE -> R.string.points_create_place
    PointAction.PLACE_VERIFIED -> R.string.points_place_verified
    PointAction.COMMENT -> R.string.points_comment
    PointAction.VOTE_RECEIVED -> R.string.points_vote_received
    PointAction.VISIT_EXPERIENCE -> R.string.points_visit_experience
    PointAction.PLACE_RESOLVED -> R.string.points_place_resolved
}
