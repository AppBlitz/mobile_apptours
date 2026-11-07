package co.edu.uniquindio.apptours.features.moderator.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*
import androidx.annotation.StringRes

/** Hoja para rechazar con motivo obligatorio (M-03). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RejectReasonSheet(
    place: Place,
    reason: String,
    @StringRes errorRes: Int?,
    enabled: Boolean,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColabSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rechazar publicación",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }

            Text(
                text = "${place.title} · por ${place.authorName}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "La publicación dejará de mostrarse en el feed público y el autor verá tu motivo en sus notificaciones.",
                fontSize = 13.sp,
                color = ColabTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Motivo del rechazo *",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ColabTextPrimary
                )
                Text(
                    text = "Obligatorio · ${reason.length} / 300",
                    fontSize = 12.sp,
                    color = ColabTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            ColabTextField(
                value = reason,
                onValueChange = { if (it.length <= 300) onReasonChange(it) },
                label = "",
                placeholder = "Explica el motivo del rechazo...",
                singleLine = false,
                minLines = 3,
                errorMessage = errorRes?.let { stringResource(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Chips rápidos de motivo de rechazo (Página 18 del PDF)
            val quickReasons = listOf(
                "Ubicación incorrecta",
                "No existe",
                "Información imprecisa",
                "Contenido inapropiado"
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickReasons.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { reasonChip ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = ColabCardSurface,
                                border = BorderStroke(1.dp, ColabGrayBorder),
                                modifier = Modifier.clickable {
                                    onReasonChange("$reasonChip: no pudimos verificar la información enviada.")
                                }
                            ) {
                                Text(
                                    text = reasonChip,
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

            Spacer(modifier = Modifier.height(20.dp))

            ColabPrimaryButton(
                text = "Confirmar rechazo",
                containerColor = ColabError,
                contentColor = Color.White,
                enabled = enabled,
                onClick = onConfirm
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = ColabTextSecondary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

}
