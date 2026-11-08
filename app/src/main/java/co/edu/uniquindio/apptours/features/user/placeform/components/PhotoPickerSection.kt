package co.edu.uniquindio.apptours.features.user.placeform.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.FieldErrorText
import co.edu.uniquindio.apptours.core.designsystem.components.MountainIllustrationHeader
import co.edu.uniquindio.apptours.core.designsystem.components.fieldBorderColor
import co.edu.uniquindio.apptours.core.designsystem.components.fieldLabelColor
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequester
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.validation.PlaceForms

// TODO(IT-8): reemplazar la simulación por el selector de fotos real.
@Composable
internal fun PhotoPickerSection(
    photosCount: Int,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    errorMessage: String?,
    requester: FieldRequester
) {
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
                    onClick = onAddPhoto,
                    enabled = photosCount < PlaceForms.MAX_PHOTOS,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColabBrown)
                ) {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.photos_upload_button), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.photos_hint, photosCount),
                    fontSize = 11.sp,
                    color = ColabTextSecondary
                )
            }
        }
        FieldErrorText(errorMessage)

        if (photosCount > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(photosCount.coerceAtMost(3)) {
                    Box(modifier = Modifier.size(84.dp)) {
                        MountainIllustrationHeader(modifier = Modifier.fillMaxSize())
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(22.dp)
                                .clickable { onRemovePhoto() },
                            shape = CircleShape,
                            color = ColabDarkGreen
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.photos_remove),
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
