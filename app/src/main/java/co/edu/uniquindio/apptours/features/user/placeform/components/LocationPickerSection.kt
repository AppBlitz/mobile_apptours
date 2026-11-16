package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabOutlinedButton
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.map.MapLocationPickerDialog
import co.edu.uniquindio.apptours.core.map.PlaceLocationMiniMap
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

/**
 * Ubicación de la publicación: muestra el punto elegido en un mini-mapa y abre el selector a
 * pantalla completa. Las coordenadas guardadas salen siempre del mapa.
 */
@Composable
internal fun LocationPickerSection(
    latitude: Double,
    longitude: Double,
    locationPicked: Boolean,
    category: PlaceCategory?,
    title: String,
    onLocationChange: (Double, Double) -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.formField(requester)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.place_form_location_label),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = fieldLabelColor(errorMessage)
            )
            Text(
                text = stringResource(R.string.place_form_location_hint),
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (locationPicked) {
            PlaceLocationMiniMap(
                latitude = latitude,
                longitude = longitude,
                category = category,
                title = title,
                mapHeight = 150.dp,
                showDirections = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.map_coordinates, latitude, longitude),
                fontSize = 12.sp,
                color = ColabDarkGreen,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Text(
                text = stringResource(R.string.place_form_location_pending),
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        ColabOutlinedButton(
            text = stringResource(if (locationPicked) R.string.map_change_point else R.string.map_pick_on_map),
            onClick = { showPicker = true },
            leadingIcon = Icons.Filled.Map,
            borderColor = if (errorMessage != null) ColabError else ColabDarkGreen,
            contentColor = if (errorMessage != null) ColabError else ColabDarkGreen
        )
        FieldErrorText(errorMessage)
    }

    if (showPicker) {
        MapLocationPickerDialog(
            title = stringResource(R.string.map_picker_title_place),
            initialLatitude = latitude.takeIf { locationPicked },
            initialLongitude = longitude.takeIf { locationPicked },
            onConfirm = { lat, lng ->
                onLocationChange(lat, lng)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }
}
