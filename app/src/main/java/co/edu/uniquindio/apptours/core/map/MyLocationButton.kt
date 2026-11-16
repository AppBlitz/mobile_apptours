package co.edu.uniquindio.apptours.core.map

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen
import co.edu.uniquindio.apptours.core.theme.ColabSurface

/** Botón "Mi ubicación" que se pone encima de los mapas. */
@Composable
fun MyLocationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(46.dp),
        containerColor = ColabSurface,
        contentColor = ColabDarkGreen,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
    ) {
        Icon(Icons.Filled.MyLocation, contentDescription = stringResource(R.string.map_my_location))
    }
}

/**
 * Pide la ubicación del teléfono y llama a [onFound]; si no hay permiso o no responde, avisa con un
 * mensaje corto y el usuario sigue eligiendo el punto a mano.
 */
@Composable
fun rememberMyLocationAction(onFound: (latitude: Double, longitude: Double) -> Unit): () -> Unit {
    val context = LocalContext.current
    return rememberDeviceLocationRequester { result ->
        when (result) {
            is DeviceLocationResult.Found -> onFound(result.latitude, result.longitude)
            DeviceLocationResult.Denied -> context.toast(R.string.location_permission_denied)
            DeviceLocationResult.Unavailable -> context.toast(R.string.location_unavailable)
        }
    }
}

/** Abre la ruta hacia el punto en la app de mapas instalada (o en el navegador si no hay ninguna). */
fun openDirections(context: Context, latitude: Double, longitude: Double, label: String) {
    val geo = Uri.parse("geo:0,0?q=$latitude,$longitude(${Uri.encode(label)})")
    val web = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, geo))
    } catch (e: ActivityNotFoundException) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, web))
        } catch (e: ActivityNotFoundException) {
            context.toast(R.string.map_directions_unavailable)
        }
    }
}

private fun Context.toast(messageRes: Int) {
    Toast.makeText(this, messageRes, Toast.LENGTH_LONG).show()
}
