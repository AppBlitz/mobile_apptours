package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.theme.ColabError
import co.edu.uniquindio.apptours.core.theme.ColabGrayBorder
import co.edu.uniquindio.apptours.core.theme.ColabTextPrimary

/** Mensaje rojo justo debajo de un campo que no es de texto (selector, chips, mapa…). */
@Composable
fun FieldErrorText(message: String?) {
    if (message == null) return
    Text(
        text = message,
        color = ColabError,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, start = 6.dp)
    )
}

/** Color del borde de un selector: rojo si tiene error. */
fun fieldBorderColor(errorMessage: String?): Color = if (errorMessage != null) ColabError else ColabGrayBorder

/** Color de la etiqueta de un campo: roja si tiene error. */
fun fieldLabelColor(errorMessage: String?): Color = if (errorMessage != null) ColabError else ColabTextPrimary
