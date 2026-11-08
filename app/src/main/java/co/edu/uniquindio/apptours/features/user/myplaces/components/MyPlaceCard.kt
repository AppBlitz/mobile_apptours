package co.edu.uniquindio.apptours.features.user.myplaces.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryBadgeChip
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.relativeTimeLabel
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

/** Tarjeta de una publicación propia: categoría, fecha, votos, visitas y, si fue rechazada, el motivo. */
@Composable
internal fun MyPlaceCard(place: Place, onClick: () -> Unit) {
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
                CategoryBadgeChip(category = place.category)
                Text(
                    text = stringResource(R.string.my_places_created_at, relativeTimeLabel(place.createdAt)),
                    fontSize = 12.sp,
                    color = ColabTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = place.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ColabTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Metric(icon = Icons.Outlined.FavoriteBorder, value = place.votesCount)
                Metric(icon = Icons.Outlined.Hiking, value = place.visitsCount)
            }

            if (place.status == PlaceStatus.PENDIENTE) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.my_places_pending_hint),
                    fontSize = 12.sp,
                    color = ColabBrown
                )
            }

            if (place.status == PlaceStatus.RECHAZADA) {
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
                            text = place.rejectionReason?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.my_places_no_reason),
                            fontSize = 13.sp,
                            color = ColabTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.my_places_rejected_hint),
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Metric(icon: ImageVector, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = ColabTextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value.toString(), fontSize = 13.sp, color = ColabTextSecondary)
    }
}
