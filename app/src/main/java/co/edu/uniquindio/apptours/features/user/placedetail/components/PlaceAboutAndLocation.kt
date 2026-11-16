package co.edu.uniquindio.apptours.features.user.placedetail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.map.PlaceLocationMiniMap
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.Place

/** Descripción, ubicación exacta con mini-mapa y botón "Cómo llegar". */
@Composable
internal fun PlaceAboutAndLocation(place: Place) {
    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = stringResource(R.string.place_detail_about),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = place.description,
        fontSize = 14.sp,
        color = ColabTextPrimary,
        lineHeight = 21.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.place_detail_exact_location),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = stringResource(R.string.map_coordinates, place.latitude, place.longitude),
            fontSize = 12.sp,
            color = ColabTextSecondary
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    PlaceLocationMiniMap(
        latitude = place.latitude,
        longitude = place.longitude,
        category = place.category,
        title = place.title
    )
}
