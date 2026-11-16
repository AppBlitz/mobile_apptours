package co.edu.uniquindio.apptours.core.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/** Resultado de pedir la ubicación del teléfono. */
sealed interface DeviceLocationResult {
    data class Found(val latitude: Double, val longitude: Double) : DeviceLocationResult
    /** El usuario no dio el permiso. */
    data object Denied : DeviceLocationResult
    /** Con permiso, pero la ubicación está apagada o no respondió. */
    data object Unavailable : DeviceLocationResult
}

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

fun hasLocationPermission(context: Context): Boolean = LOCATION_PERMISSIONS.any {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * Devuelve una función que pide el permiso de ubicación (si falta) y luego la ubicación actual.
 * La ubicación precisa o la aproximada sirven igual: solo se usa para centrar el mapa y "Cercanos".
 */
@Composable
fun rememberDeviceLocationRequester(onResult: (DeviceLocationResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnResult = rememberUpdatedState(onResult)

    val fetch: () -> Unit = {
        scope.launch { currentOnResult.value(currentLocation(context)) }
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) fetch() else currentOnResult.value(DeviceLocationResult.Denied)
    }

    return {
        if (hasLocationPermission(context)) fetch() else launcher.launch(LOCATION_PERMISSIONS)
    }
}

@SuppressLint("MissingPermission") // Se llama solo después de comprobar el permiso.
private suspend fun currentLocation(context: Context): DeviceLocationResult {
    if (!hasLocationPermission(context)) return DeviceLocationResult.Denied
    return try {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val location = client.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        ).await() ?: client.lastLocation.await()
        if (location != null) {
            DeviceLocationResult.Found(location.latitude, location.longitude)
        } else {
            DeviceLocationResult.Unavailable
        }
    } catch (e: Exception) {
        DeviceLocationResult.Unavailable
    }
}
