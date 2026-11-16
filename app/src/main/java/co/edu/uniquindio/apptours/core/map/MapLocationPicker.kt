package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.ColabPrimaryButton
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.DefaultLocation
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlin.math.roundToLong

/**
 * Selector de ubicación a pantalla completa: el pin queda fijo en el centro y el usuario mueve el
 * mapa (o lo toca para llevar el pin a ese punto). Lo usan el registro, editar perfil y el
 * formulario de publicación; lo que se guarda en la base de datos es el punto confirmado aquí.
 *
 * @param initialLatitude punto ya elegido, o null para abrir en la ciudad por defecto.
 */
@Composable
fun MapLocationPickerDialog(
    title: String,
    initialLatitude: Double?,
    initialLongitude: Double?,
    onConfirm: (latitude: Double, longitude: Double) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColabBackground)
                .systemBarsPadding()
        ) {
            PickerTopBar(title = title, onClose = onDismiss)
            if (MapDefaults.enabled) {
                PickerContent(initialLatitude, initialLongitude, onConfirm)
            } else {
                MapUnavailablePlaceholder(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun PickerTopBar(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColabDarkGreen)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.map_picker_close),
                tint = ColabSurface
            )
        }
        Text(text = title, color = ColabSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
private fun ColumnScope.PickerContent(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onConfirm: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val hasInitial = initialLatitude != null && initialLongitude != null
    val viewport = rememberMapViewportState {
        setCameraOptions(
            if (hasInitial) {
                MapDefaults.camera(initialLatitude!!, initialLongitude!!, MapDefaults.PICKER_ZOOM)
            } else {
                MapDefaults.camera(DefaultLocation.LATITUDE, DefaultLocation.LONGITUDE, MapDefaults.CITY_ZOOM)
            }
        )
    }
    var showPuck by remember { mutableStateOf(hasLocationPermission(context)) }
    val goToMyLocation = rememberMyLocationAction { lat, lng ->
        showPuck = true
        viewport.flyTo(MapDefaults.camera(lat, lng, MapDefaults.PICKER_ZOOM))
    }
    // Sin punto previo y con permiso ya dado, se abre directamente donde está el usuario.
    LaunchedEffect(Unit) {
        if (!hasInitial && hasLocationPermission(context)) goToMyLocation()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = viewport,
            scaleBar = {},
            onMapClickListener = OnMapClickListener { point ->
                viewport.easeTo(CameraOptions.Builder().center(point).build())
                true
            }
        ) {
            MapEffect(showPuck) { mapView ->
                mapView.location.updateSettings {
                    enabled = showPuck
                    locationPuck = createDefault2DPuck(withBearing = false)
                }
            }
        }
        // La punta del pin queda justo en el centro del mapa.
        CenterPin(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-23).dp)
        )
        MyLocationButton(
            onClick = goToMyLocation,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        )
    }

    PickerFooter(viewport = viewport, onConfirm = onConfirm)
}

@Composable
private fun PickerFooter(viewport: MapViewportState, onConfirm: (Double, Double) -> Unit) {
    val center = viewport.cameraState?.center
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColabSurface)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.map_picker_hint),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (center != null) {
            Surface(shape = RoundedCornerShape(12.dp), color = ColabSageBg) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Place,
                        contentDescription = null,
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.map_coordinates, center.latitude(), center.longitude()),
                        color = ColabDarkGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        ColabPrimaryButton(
            text = stringResource(R.string.map_picker_confirm),
            onClick = {
                center?.let { onConfirm(it.latitude().round6(), it.longitude().round6()) }
            },
            enabled = center != null
        )
    }
}

/** Seis decimales (~10 cm) bastan y evitan guardar ruido de la cámara. */
private fun Double.round6(): Double = (this * 1_000_000).roundToLong() / 1_000_000.0
