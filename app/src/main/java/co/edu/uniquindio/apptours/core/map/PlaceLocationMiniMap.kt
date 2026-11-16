package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabOutlinedButton
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions

/**
 * Mini-mapa fijo en la ubicación exacta del lugar y botón "Cómo llegar".
 * Una capa transparente encima evita que el mapa se robe el desplazamiento de la pantalla.
 */
@Composable
fun PlaceLocationMiniMap(
    latitude: Double,
    longitude: Double,
    /** null mientras el formulario aún no tiene categoría: se dibuja un punto simple. */
    category: PlaceCategory?,
    title: String,
    modifier: Modifier = Modifier,
    mapHeight: Dp = 160.dp,
    showDirections: Boolean = true
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        val mapModifier = Modifier
            .fillMaxWidth()
            .height(mapHeight)
            .clip(RoundedCornerShape(18.dp))
        if (MapDefaults.enabled) {
            Box(modifier = mapModifier) {
                // La llave reinicia la cámara si cambian las coordenadas (p. ej. al recargar el lugar).
                val viewport = rememberMapViewportState(key = "$latitude,$longitude") {
                    setCameraOptions(MapDefaults.camera(latitude, longitude, MapDefaults.PLACE_ZOOM))
                }
                MapboxMap(
                    modifier = Modifier.matchParentSize(),
                    mapViewportState = viewport,
                    compass = {},
                    scaleBar = {}
                ) {
                    val options = remember(latitude, longitude) {
                        viewAnnotationOptions {
                            geometry(MapDefaults.point(latitude, longitude))
                            allowOverlap(true)
                        }
                    }
                    ViewAnnotation(options = options) {
                        if (category != null) {
                            CategoryMarker(category = category, selected = false, contentDescription = title)
                        } else {
                            PointMarker()
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) awaitPointerEvent()
                            }
                        }
                )
            }
        } else {
            MapUnavailablePlaceholder(mapModifier)
        }

        if (showDirections) {
            Spacer(modifier = Modifier.height(12.dp))
            ColabOutlinedButton(
                text = stringResource(R.string.map_directions),
                onClick = { openDirections(context, latitude, longitude, title) },
                leadingIcon = Icons.Filled.NearMe
            )
        }
    }
}
