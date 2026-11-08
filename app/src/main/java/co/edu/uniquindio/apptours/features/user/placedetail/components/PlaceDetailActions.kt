package co.edu.uniquindio.apptours.features.user.placedetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

/** "Me interesa", "Visitado", contar la experiencia y acciones del autor (editar / finalizar / eliminar). */
@Composable
internal fun PlaceDetailActions(
    place: Place,
    isInterested: Boolean,
    isVisited: Boolean,
    isOwner: Boolean,
    onToggleInterest: () -> Unit,
    onToggleVisited: () -> Unit,
    onOpenExperience: () -> Unit,
    onEditPlace: () -> Unit,
    onMarkResolved: () -> Unit,
    onDeletePlace: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onToggleInterest,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isInterested) ColabAmberBg else ColabSurface
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = ColabBrown,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.place_interest_count, place.votesCount),
                color = ColabTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        OutlinedButton(
            onClick = onToggleVisited,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isVisited) ColabSageBg else ColabSurface
            )
        ) {
            Icon(
                imageVector = if (isVisited) Icons.Filled.CheckCircle else Icons.Filled.CheckCircleOutline,
                contentDescription = null,
                tint = ColabDarkGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(if (isVisited) R.string.place_visited else R.string.place_mark_visited),
                color = ColabDarkGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (isVisited) {
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onOpenExperience) {
            Icon(Icons.Outlined.RateReview, contentDescription = null, tint = ColabMediumGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.place_share_experience),
                color = ColabMediumGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }

    if (isOwner) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OwnerAction(
                text = stringResource(R.string.place_action_edit),
                icon = Icons.Outlined.Edit,
                onClick = onEditPlace,
                modifier = Modifier.weight(1f)
            )
            // Solo lo verificado se puede finalizar (lo pendiente o rechazado aún no pasó moderación).
            if (place.status == PlaceStatus.VERIFICADA) {
                OwnerAction(
                    text = stringResource(R.string.place_action_resolve),
                    icon = Icons.Outlined.CheckCircle,
                    onClick = onMarkResolved,
                    modifier = Modifier.weight(1f)
                )
            }
            OwnerAction(
                text = stringResource(R.string.action_delete),
                icon = Icons.Outlined.Delete,
                onClick = onDeletePlace,
                modifier = Modifier.weight(1f),
                isDestructive = true
            )
        }
    }
}

@Composable
private fun OwnerAction(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    val color = if (isDestructive) ColabError else ColabDarkGreen
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isDestructive) ColabError else ColabGrayBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = color, maxLines = 1)
    }
}
