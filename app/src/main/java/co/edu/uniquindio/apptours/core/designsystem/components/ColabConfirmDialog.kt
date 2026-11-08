package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import co.edu.uniquindio.apptours.R
import androidx.compose.ui.text.font.FontWeight
import co.edu.uniquindio.apptours.core.theme.ColabError
import co.edu.uniquindio.apptours.core.theme.ColabSurface
import co.edu.uniquindio.apptours.core.theme.ColabTextPrimary
import co.edu.uniquindio.apptours.core.theme.ColabTextSecondary

/** Confirmación antes de eliminar una publicación (la usan el detalle y el formulario de edición). */
@Composable
fun DeletePlaceDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ColabConfirmDialog(
        title = stringResource(R.string.place_delete_title),
        message = stringResource(R.string.place_delete_body),
        confirmText = stringResource(R.string.action_delete),
        dismissText = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

/** Diálogo de confirmación para acciones que no se pueden deshacer (eliminar, etc.). */
@Composable
fun ColabConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = ColabError
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ColabSurface,
        title = { Text(title, fontWeight = FontWeight.Bold, color = ColabTextPrimary) },
        text = { Text(message, color = ColabTextSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = confirmColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = ColabTextSecondary)
            }
        }
    )
}
