package co.edu.uniquindio.apptours.features.user.placedetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.messageOf
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import co.edu.uniquindio.apptours.features.user.placedetail.VisitExperienceState

/** "Comparte tu experiencia" (T-05): calificación de 1 a 5 (obligatoria) y texto opcional. */
@Composable
internal fun VisitExperienceDialog(
    state: VisitExperienceState,
    onRatingChange: (Int) -> Unit,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val ratingError = state.form.messageOf(FormField.RATING)
    val textError = state.form.messageOf(FormField.COMMENT)

    AlertDialog(
        onDismissRequest = { if (!state.isSaving) onDismiss() },
        containerColor = ColabSurface,
        title = {
            Text(
                text = stringResource(R.string.experience_title),
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.experience_body),
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.experience_rating_label),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = fieldLabelColor(ratingError)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    (PlaceForms.MIN_RATING..PlaceForms.MAX_RATING).forEach { star ->
                        val selected = state.rating != null && star <= state.rating
                        Icon(
                            imageVector = if (selected) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = stringResource(R.string.rating_description, star, PlaceForms.MAX_RATING),
                            tint = if (ratingError != null && !selected) ColabError else ColabBrown,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(enabled = !state.isSaving) { onRatingChange(star) }
                                .padding(4.dp)
                        )
                    }
                }
                FieldErrorText(ratingError)
                Spacer(modifier = Modifier.height(12.dp))
                ColabTextField(
                    value = state.text,
                    onValueChange = onTextChange,
                    label = stringResource(R.string.experience_text_label),
                    placeholder = stringResource(R.string.experience_text_placeholder),
                    singleLine = false,
                    minLines = 3,
                    enabled = !state.isSaving,
                    errorMessage = textError
                )
                Text(
                    text = stringResource(
                        R.string.place_form_description_counter,
                        state.text.trim().length,
                        PlaceForms.MAX_COMMENT_LENGTH
                    ),
                    fontSize = 11.sp,
                    color = if (textError != null) ColabError else ColabTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, start = 6.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit, enabled = !state.isSaving) {
                Text(
                    text = stringResource(R.string.experience_submit),
                    color = ColabDarkGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.isSaving) {
                Text(stringResource(R.string.experience_skip), color = ColabTextSecondary)
            }
        }
    )
}
