package co.edu.uniquindio.apptours.features.user.recommendations.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.RecommendationResult
import kotlin.math.roundToInt

/**
 * Explica de dónde salen las recomendaciones: afinidad por categoría y zonas frecuentes,
 * o el aviso de arranque en frío si el usuario aún tiene pocas visitas.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TasteSummary(result: RecommendationResult, usingDeviceLocation: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (result.isColdStart) ColabAmberBg else ColabSageBg
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (result.isColdStart) Icons.Outlined.Info else Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = if (result.isColdStart) ColabBrown else ColabDarkGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (result.isColdStart) R.string.recommendations_cold_start_title else R.string.recommendations_tastes
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ColabTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (result.isColdStart) {
                Text(
                    text = pluralStringResource(R.plurals.recommendations_cold_start, result.totalVisits, result.totalVisits),
                    fontSize = 13.sp,
                    color = ColabTextPrimary
                )
            } else {
                Text(
                    text = pluralStringResource(
                        R.plurals.recommendations_based_on, result.totalVisits, result.totalVisits, result.zones.size
                    ),
                    fontSize = 13.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    result.categoryAffinity.entries
                        .sortedByDescending { it.value }
                        .forEach { (category, affinity) ->
                            Surface(shape = RoundedCornerShape(12.dp), color = ColabSurface) {
                                Text(
                                    text = stringResource(
                                        R.string.recommendations_affinity_chip,
                                        stringResource(category.labelRes()),
                                        (affinity * 100).roundToInt()
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColabDarkGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (usingDeviceLocation) R.string.recommendations_origin_device else R.string.recommendations_origin_saved
                ),
                fontSize = 11.sp,
                color = ColabTextSecondary
            )
        }
    }
}
