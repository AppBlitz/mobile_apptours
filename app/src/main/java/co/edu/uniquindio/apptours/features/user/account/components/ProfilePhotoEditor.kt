package co.edu.uniquindio.apptours.features.user.account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.PhotoSourceSheet
import co.edu.uniquindio.apptours.core.designsystem.components.UserAvatar
import co.edu.uniquindio.apptours.core.designsystem.components.rememberPhotoSourceLauncher
import co.edu.uniquindio.apptours.core.theme.*

/**
 * Avatar con el botón de cámara. Al tocarlo se elige entre tomar una foto o buscarla en la galería;
 * mientras se carga se ve el porcentaje y no se puede volver a cambiar.
 */
@Composable
internal fun ProfilePhotoEditor(
    photoUrl: String,
    initials: String,
    uploadProgress: Float?,
    enabled: Boolean,
    onPhotoPicked: (String) -> Unit,
    onRemovePhoto: () -> Unit
) {
    var showSourceSheet by rememberSaveable { mutableStateOf(false) }
    val photoSource = rememberPhotoSourceLauncher(maxPhotos = 1) { uris ->
        uris.firstOrNull()?.let(onPhotoPicked)
    }
    val canChange = enabled && uploadProgress == null
    val open = { if (canChange) showSourceSheet = true }

    if (showSourceSheet) {
        PhotoSourceSheet(
            launcher = photoSource,
            onDismiss = { showSourceSheet = false },
            onRemove = if (photoUrl.isNotBlank()) onRemovePhoto else null
        )
    }

    Box(
        modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .clickable(enabled = canChange, onClick = open)
    ) {
        UserAvatar(
            photoUrl = photoUrl,
            initials = initials,
            fontSize = 30.sp,
            contentDescription = stringResource(R.string.account_photo_description),
            border = BorderStroke(2.dp, ColabLightGreen),
            modifier = Modifier.fillMaxSize()
        )
        if (uploadProgress != null) {
            CircularProgressIndicator(
                progress = { uploadProgress },
                color = ColabLightGreen,
                trackColor = Color.White.copy(alpha = 0.4f),
                strokeWidth = 4.dp,
                modifier = Modifier.fillMaxSize()
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp),
            shape = CircleShape,
            color = ColabDarkGreen,
            border = BorderStroke(2.dp, Color.White)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    TextButton(onClick = open, enabled = canChange) {
        Text(
            text = if (uploadProgress != null) {
                stringResource(R.string.account_photo_uploading, (uploadProgress * 100).toInt())
            } else {
                stringResource(R.string.account_change_photo)
            },
            color = ColabDarkGreen,
            fontWeight = FontWeight.SemiBold
        )
    }
}
