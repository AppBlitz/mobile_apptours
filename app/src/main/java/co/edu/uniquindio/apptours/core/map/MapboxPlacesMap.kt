package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.apptours.domain.model.Place
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

/**
 * Mapa del feed: un marcador por lugar con el color de su categoría. Tocar un marcador lo selecciona
 * (la tarjeta del lugar la dibuja quien usa el mapa). Incluye el botón "Mi ubicación".
 */
@Composable
fun MapboxPlacesMap(
    places: List<Place>,
    selectedPlaceId: String?,
    onPlaceSelected: (Place) -> Unit,
    initialLatitude: Double,
    initialLongitude: Double,
    modifier: Modifier = Modifier
) {
    if (!MapDefaults.enabled) {
        MapUnavailablePlaceholder(modifier)
        return
    }
    val context = LocalContext.current
    val viewport = rememberMapViewportState {
        setCameraOptions(MapDefaults.camera(initialLatitude, initialLongitude, MapDefaults.CITY_ZOOM))
    }
    var showPuck by remember { mutableStateOf(hasLocationPermission(context)) }
    val goToMyLocation = rememberMyLocationAction { lat, lng ->
        showPuck = true
        viewport.flyTo(MapDefaults.camera(lat, lng, MapDefaults.CITY_ZOOM + 1))
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = viewport,
            scaleBar = {}
        ) {
            MapEffect(showPuck) { mapView ->
                mapView.location.updateSettings {
                    enabled = showPuck
                    locationPuck = createDefault2DPuck(withBearing = false)
                }
            }
            places.forEach { place ->
                val options = remember(place.id, place.latitude, place.longitude) {
                    viewAnnotationOptions {
                        geometry(MapDefaults.point(place.latitude, place.longitude))
                        allowOverlap(true)
                    }
                }
                ViewAnnotation(options = options) {
                    CategoryMarker(
                        category = place.category,
                        selected = place.id == selectedPlaceId,
                        contentDescription = place.title,
                        modifier = Modifier.clickable { onPlaceSelected(place) }
                    )
                }
            }
        }
        MyLocationButton(
            onClick = goToMyLocation,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
    }
}
