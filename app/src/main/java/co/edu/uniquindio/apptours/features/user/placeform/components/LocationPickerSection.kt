package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.map.SimulatedInteractiveMap
import co.edu.uniquindio.apptours.core.theme.*

@Composable
internal fun LocationPickerSection(
    latitude: Double,
    longitude: Double,
    locationPicked: Boolean,
    onLocationChange: (Double, Double) -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
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

        SimulatedInteractiveMap(
            places = emptyList(),
            selectedPlace = null,
            onPlaceSelected = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .then(
                    if (errorMessage != null) {
                        Modifier.border(2.dp, ColabError, RoundedCornerShape(18.dp))
                    } else {
                        Modifier
                    }
                ),
            interactivePointSelection = true,
            selectedLat = latitude,
            selectedLng = longitude,
            onCoordinatesChanged = { lat, lng -> onLocationChange(lat, lng) }
        )
        FieldErrorText(errorMessage)

        Spacer(modifier = Modifier.height(10.dp))

        if (locationPicked) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CoordinateChip(text = "Lat $latitude", modifier = Modifier.weight(1f))
                CoordinateChip(text = "Lng $longitude", modifier = Modifier.weight(1f))
            }
        } else {
            Text(
                text = stringResource(R.string.place_form_location_pending),
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
    }
}

@Composable
private fun CoordinateChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = ColabSageBg
    ) {
        Text(
            text = text,
            color = ColabDarkGreen,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}
