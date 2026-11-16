package co.edu.uniquindio.apptours.core.designsystem.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import java.io.File
import java.util.UUID

/**
 * Lanzadores para conseguir una foto con la cámara o con el selector de la galería (ninguno pide permisos).
 * Se crean en una pantalla que sigue visible mientras la cámara está abierta, así el resultado no se pierde.
 */
class PhotoSourceLauncher internal constructor(
    val takePhoto: () -> Unit,
    val pickFromGallery: () -> Unit
)

/**
 * [maxPhotos] limita cuántas fotos se pueden elegir de la galería de una vez (la cámara siempre da una).
 * [onPicked] recibe las direcciones locales (content://) de las fotos nuevas.
 */
@Composable
fun rememberPhotoSourceLauncher(maxPhotos: Int, onPicked: (List<String>) -> Unit): PhotoSourceLauncher {
    val context = LocalContext.current
    val currentOnPicked by rememberUpdatedState(onPicked)
    // Se guarda para que la foto no se pierda si el sistema recrea la pantalla mientras la cámara está abierta.
    var pendingCameraUri by rememberSaveable { mutableStateOf<String?>(null) }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        val uri = pendingCameraUri
        pendingCameraUri = null
        if (uri == null) return@rememberLauncherForActivityResult
        if (saved) currentOnPicked(listOf(uri)) else deleteCameraFile(context, uri)
    }
    // El selector múltiple exige un máximo de 2 o más; si solo cabe una foto se usa el sencillo.
    val pickMany = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxPhotos.coerceAtLeast(2))
    ) { uris -> currentOnPicked(uris.take(maxPhotos).map { it.toString() }) }
    val pickOne = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        currentOnPicked(listOfNotNull(uri?.toString()))
    }

    return remember(maxPhotos) {
        PhotoSourceLauncher(
            takePhoto = {
                val uri = newCameraUri(context)
                pendingCameraUri = uri.toString()
                try {
                    camera.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    pendingCameraUri = null
                    Toast.makeText(context, R.string.photos_camera_unavailable, Toast.LENGTH_LONG).show()
                } catch (e: SecurityException) {
                    pendingCameraUri = null
                    Toast.makeText(context, R.string.photos_camera_unavailable, Toast.LENGTH_LONG).show()
                }
            },
            pickFromGallery = {
                val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                if (maxPhotos > 1) pickMany.launch(request) else pickOne.launch(request)
            }
        )
    }
}

/** Hoja inferior con las opciones "Tomar foto" y "Elegir de la galería" (y "Quitar foto" si se pide). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSourceSheet(
    launcher: PhotoSourceLauncher,
    onDismiss: () -> Unit,
    onRemove: (() -> Unit)? = null
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = ColabSurface) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 28.dp)) {
            Text(
                text = stringResource(R.string.photos_source_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(14.dp))
            PhotoSourceOption(Icons.Outlined.PhotoCamera, stringResource(R.string.photos_take_photo)) {
                onDismiss()
                launcher.takePhoto()
            }
            Spacer(modifier = Modifier.height(10.dp))
            PhotoSourceOption(Icons.Outlined.PhotoLibrary, stringResource(R.string.photos_from_gallery)) {
                onDismiss()
                launcher.pickFromGallery()
            }
            if (onRemove != null) {
                Spacer(modifier = Modifier.height(10.dp))
                PhotoSourceOption(Icons.Outlined.Delete, stringResource(R.string.photos_remove), ColabError) {
                    onDismiss()
                    onRemove()
                }
            }
        }
    }
}

@Composable
private fun PhotoSourceOption(
    icon: ImageVector,
    text: String,
    tint: Color = ColabDarkGreen,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = ColabSageBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = text, color = tint, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

/** Archivo vacío en la caché de la app donde la cámara guarda la foto; se comparte con FileProvider. */
private fun newCameraUri(context: Context): Uri {
    val dir = File(context.cacheDir, CAMERA_DIR).apply { mkdirs() }
    val file = File(dir, "${UUID.randomUUID()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun deleteCameraFile(context: Context, uri: String) {
    runCatching { context.contentResolver.delete(Uri.parse(uri), null, null) }
}

private const val CAMERA_DIR = "camera"
