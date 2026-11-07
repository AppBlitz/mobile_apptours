package co.edu.uniquindio.apptours.features.user.placeform.components

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

/** Horario de atención sugerido: días activos y horas de apertura / cierre. */
@Composable
internal fun ScheduleSelector(
    selectedDays: List<String>,
    onToggleDay: (String) -> Unit,
    openTime: String,
    onOpenTimeChange: (String) -> Unit,
    closeTime: String,
    onCloseTimeChange: (String) -> Unit
) {

    Text(
        text = "Horario de atención sugerido",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = ColabTextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Place.ALL_DAYS.forEach { day ->
            val active = selectedDays.contains(day)
            Surface(
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        onToggleDay(day)
                    },
                shape = CircleShape,
                color = if (active) ColabDarkGreen else ColabSurface,
                border = BorderStroke(1.dp, ColabGrayBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = day,
                        color = if (active) Color.White else ColabTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ColabTextField(
            value = openTime,
            onValueChange = onOpenTimeChange,
            label = "",
            placeholder = "7:00 a. m.",
            leadingIcon = Icons.Outlined.Schedule,
            modifier = Modifier.weight(1f)
        )
        ColabTextField(
            value = closeTime,
            onValueChange = onCloseTimeChange,
            label = "",
            placeholder = "7:00 p. m.",
            leadingIcon = Icons.Outlined.Schedule,
            modifier = Modifier.weight(1f)
        )
    }

}
