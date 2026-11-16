package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*

/**
 * Barra superior y aviso de estado: al crear dice que nace "Pendiente de verificación";
 * al editar algo ya revisado avisa que vuelve a revisión y sale del feed.
 */
@Composable
internal fun PlaceFormHeader(
    isEditing: Boolean,
    returnsToReview: Boolean,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.action_close),
                tint = ColabDarkGreen
            )
        }
        Text(
            text = stringResource(if (isEditing) R.string.place_form_header_edit else R.string.place_form_header_create),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    val (title, body) = when {
        returnsToReview -> R.string.place_form_edit_review_title to R.string.place_form_edit_review_body
        isEditing -> R.string.place_form_edit_pending_title to R.string.place_form_pending_body
        else -> R.string.place_form_pending_title to R.string.place_form_pending_body
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabAmberBg
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (returnsToReview) Icons.AutoMirrored.Outlined.Undo else Icons.Outlined.Schedule,
                contentDescription = null,
                tint = ColabBrown,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(body),
                    fontSize = 12.sp,
                    color = ColabTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
