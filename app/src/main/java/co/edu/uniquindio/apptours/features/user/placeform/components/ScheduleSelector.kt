package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.util.dayShortRes
import co.edu.uniquindio.apptours.core.designsystem.components.ColabTextField
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.fieldBorderColor
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.Place

/** Horario de atención sugerido: días activos y horas de apertura / cierre, cada uno con su error. */
@Composable
internal fun ScheduleSelector(
    selectedDays: List<String>,
    onToggleDay: (String) -> Unit,
    openTime: String,
    onOpenTimeChange: (String) -> Unit,
    closeTime: String,
    onCloseTimeChange: (String) -> Unit,
    daysError: String?,
    openTimeError: String?,
    closeTimeError: String?,
    daysRequester: FieldRequester,
    openTimeRequester: FieldRequester,
    closeTimeRequester: FieldRequester,
    onOpenTimeFocusLost: () -> Unit,
    onCloseTimeFocusLost: () -> Unit
) {
    Column(modifier = Modifier.formField(daysRequester)) {
        Text(
            text = stringResource(R.string.place_form_schedule_label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = fieldLabelColor(daysError)
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
                        .clickable { onToggleDay(day) },
                    shape = CircleShape,
                    color = if (active) ColabDarkGreen else ColabSurface,
                    border = BorderStroke(1.dp, fieldBorderColor(daysError))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(dayShortRes(day)),
                            color = if (active) Color.White else ColabTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        FieldErrorText(daysError)
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ColabTextField(
            value = openTime,
            onValueChange = onOpenTimeChange,
            label = stringResource(R.string.place_form_open_label),
            placeholder = stringResource(R.string.time_placeholder_open),
            leadingIcon = Icons.Outlined.Schedule,
            errorMessage = openTimeError,
            fieldRequester = openTimeRequester,
            onFocusLost = onOpenTimeFocusLost,
            modifier = Modifier.weight(1f)
        )
        ColabTextField(
            value = closeTime,
            onValueChange = onCloseTimeChange,
            label = stringResource(R.string.place_form_close_label),
            placeholder = stringResource(R.string.time_placeholder_close),
            leadingIcon = Icons.Outlined.Schedule,
            errorMessage = closeTimeError,
            fieldRequester = closeTimeRequester,
            onFocusLost = onCloseTimeFocusLost,
            modifier = Modifier.weight(1f)
        )
    }
}
