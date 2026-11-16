package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabBackground
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen
import co.edu.uniquindio.apptours.core.theme.ColabError
import co.edu.uniquindio.apptours.core.theme.ColabSageBg
import co.edu.uniquindio.apptours.core.theme.ColabTextPrimary
import co.edu.uniquindio.apptours.core.theme.ColabTextSecondary

/** Indicador de carga a pantalla completa; bloquea los toques mientras está visible. */
@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    dimBackground: Boolean = false,
    /** Avance 0..1 si se conoce (p. ej. al cargar fotos): muestra barra y porcentaje en lugar del círculo. */
    progress: Float? = null,
    @StringRes messageRes: Int = R.string.loading
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (dimBackground) Modifier.background(ColabBackground.copy(alpha = 0.7f)) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (progress == null) {
                CircularProgressIndicator(color = ColabDarkGreen)
            } else {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.width(220.dp),
                    color = ColabDarkGreen,
                    trackColor = ColabSageBg
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (progress == null) {
                    stringResource(messageRes)
                } else {
                    stringResource(R.string.photos_uploading_progress, stringResource(messageRes), (progress * 100).toInt())
                },
                color = ColabTextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

/** Mensaje de error de carga con botón para reintentar. */
@Composable
fun ErrorMessage(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = ColabError,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = stringResource(R.string.error_load_failed),
                color = ColabTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(messageRes),
                color = ColabTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            ColabOutlinedButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                leadingIcon = Icons.Outlined.Refresh
            )
        }
    }
}
