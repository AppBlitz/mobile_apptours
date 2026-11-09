package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.BuildConfig
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabSageBg
import co.edu.uniquindio.apptours.core.theme.ColabTextSecondary
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions

/** Valores comunes de los mapas de la app. */
object MapDefaults {
    /** false si no hay token en local.properties: se muestra un aviso en vez del mapa. */
    val enabled: Boolean = BuildConfig.MAPS_ENABLED

    const val CITY_ZOOM = 12.5
    const val PLACE_ZOOM = 15.5
    const val PICKER_ZOOM = 16.0

    fun point(latitude: Double, longitude: Double): Point = Point.fromLngLat(longitude, latitude)

    fun camera(latitude: Double, longitude: Double, zoom: Double): CameraOptions =
        CameraOptions.Builder()
            .center(point(latitude, longitude))
            .zoom(zoom)
            .build()
}

/** Se muestra en lugar del mapa cuando no está configurado. */
@Composable
fun MapUnavailablePlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(ColabSageBg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                tint = ColabTextSecondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.map_unavailable),
                color = ColabTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
