package co.edu.uniquindio.apptours.features.moderator.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabPrimaryButton
import co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.validation.ModerationForms

/** Motivos rápidos: se agregan al texto para que el moderador los complete. */
private val QUICK_REASONS = listOf(
    R.string.reject_quick_location,
    R.string.reject_quick_not_exists,
    R.string.reject_quick_inaccurate,
    R.string.reject_quick_inappropriate
)

/** Hoja para rechazar con motivo obligatorio (M-03) y validación en vivo (4.3). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RejectReasonSheet(
    place: Place,
    reason: String,
    reasonError: String?,
    generalError: String?,
    enabled: Boolean,
    requester: FieldRequester,
    onReasonChange: (String) -> Unit,
    onQuickReason: (String) -> Unit,
    onReasonFocusLost: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { if (enabled) onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = ColabSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.reject_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                IconButton(onClick = onDismiss, enabled = enabled) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_close))
                }
            }

            Text(
                text = stringResource(R.string.reject_place_by, place.title, place.authorName),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.reject_body),
                fontSize = 13.sp,
                color = ColabTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.reject_reason_label),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = fieldLabelColor(reasonError)
                )
                Text(
                    text = stringResource(
                        R.string.reject_reason_counter,
                        reason.trim().length,
                        ModerationForms.MAX_REASON_LENGTH
                    ),
                    fontSize = 12.sp,
                    color = if (reasonError != null) ColabError else ColabTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            ColabTextField(
                value = reason,
                onValueChange = onReasonChange,
                label = "",
                placeholder = stringResource(R.string.reject_reason_placeholder),
                singleLine = false,
                minLines = 3,
                enabled = enabled,
                errorMessage = reasonError,
                fieldRequester = requester,
                onFocusLost = onReasonFocusLost
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QUICK_REASONS.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { reasonRes ->
                            val text = stringResource(reasonRes)
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = ColabCardSurface,
                                border = BorderStroke(1.dp, ColabGrayBorder),
                                modifier = Modifier.clickable(enabled = enabled) { onQuickReason(text) }
                            ) {
                                Text(
                                    text = text,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColabTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (generalError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = generalError, color = ColabError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            ColabPrimaryButton(
                text = stringResource(R.string.reject_confirm),
                containerColor = ColabError,
                contentColor = Color.White,
                enabled = enabled,
                onClick = onConfirm
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_cancel), color = ColabTextSecondary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
