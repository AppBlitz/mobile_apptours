package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.AsyncPlaceImage
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.PhotoSourceSheet
import co.edu.uniquindio.apptours.core.designsystem.components.rememberPhotoSourceLauncher
import co.edu.uniquindio.apptours.core.designsystem.components.fieldBorderColor
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.validation.PlaceForms

/**
 * Fotos de la publicación: se toman con la cámara o se eligen de la galería (sin permisos de almacenamiento).
 * Muestra la vista previa de cada foto con su botón para quitarla; la primera es la portada.
 */
@Composable
internal fun PhotoPickerSection(
    photos: List<String>,
    enabled: Boolean,
    onPhotosPicked: (List<String>) -> Unit,
    onRemovePhoto: (String) -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
    val remaining = PlaceForms.MAX_PHOTOS - photos.size
    var showSourceSheet by rememberSaveable { mutableStateOf(false) }
    val photoSource = rememberPhotoSourceLauncher(maxPhotos = remaining.coerceAtLeast(1), onPicked = onPhotosPicked)
    val openPicker = { showSourceSheet = true }

    if (showSourceSheet) {
        PhotoSourceSheet(launcher = photoSource, onDismiss = { showSourceSheet = false })
    }

    Column(modifier = Modifier.formField(requester)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.place_form_photos_label),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = fieldLabelColor(errorMessage)
            )
            Text(
                text = stringResource(R.string.place_form_photos_limits, PlaceForms.MAX_PHOTOS),
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, fieldBorderColor(errorMessage), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = ColabSurface
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = ColabSageBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Upload,
                            contentDescription = null,
                            tint = ColabDarkGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = openPicker,
                    enabled = enabled && remaining > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColabBrown)
                ) {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.photos_upload_button), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (remaining > 0) {
                        stringResource(R.string.photos_hint, photos.size, PlaceForms.MAX_PHOTOS)
                    } else {
                        stringResource(R.string.photos_limit_reached, PlaceForms.MAX_PHOTOS)
                    },
                    fontSize = 11.sp,
                    color = ColabTextSecondary
                )
            }
        }
        FieldErrorText(errorMessage)

        if (photos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                photos.forEachIndexed { index, photo ->
                    PhotoThumbnail(
                        photo = photo,
                        isCover = index == 0,
                        enabled = enabled,
                        position = index + 1,
                        onRemove = { onRemovePhoto(photo) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(
    photo: String,
    isCover: Boolean,
    enabled: Boolean,
    position: Int,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncPlaceImage(
            url = photo,
            contentDescription = stringResource(R.string.photo_preview, position),
            modifier = Modifier.fillMaxSize()
        )
        if (isCover) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp),
                shape = RoundedCornerShape(8.dp),
                color = ColabDarkGreen.copy(alpha = 0.85f)
            ) {
                Text(
                    text = stringResource(R.string.photos_cover),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        if (enabled) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onRemove),
                shape = CircleShape,
                color = ColabDarkGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.photos_remove),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
