package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabGrayBorder
import co.edu.uniquindio.apptours.core.theme.ColabSurface
import co.edu.uniquindio.apptours.core.theme.ColabTextSecondary

/** Botón cuadrado pequeño con el logo de Google. */
@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = ColabSurface,
        border = BorderStroke(1.dp, ColabGrayBorder),
        shadowElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.ic_google_logo),
                contentDescription = stringResource(R.string.action_continue_with_google),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/** Separador "──── o continúa con ────" encima de los botones de otros proveedores. */
@Composable
fun AuthAlternativeDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = ColabGrayBorder)
        Text(
            text = stringResource(R.string.auth_or_continue_with),
            color = ColabTextSecondary,
            fontSize = 12.sp
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = ColabGrayBorder)
    }
}
