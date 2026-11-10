package co.edu.uniquindio.apptours.features.user.placedetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.PlacePhotoCarousel
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen
import co.edu.uniquindio.apptours.core.theme.ColabSurface

/** Galería superior (carrusel con "1 / N") y botón atrás. */
@Composable
internal fun PlaceDetailHeader(
    imageUrls: List<String>,
    placeTitle: String,
    onNavigateBack: () -> Unit
) {
    PlacePhotoCarousel(
        imageUrls = imageUrls,
        placeTitle = placeTitle,
        counterAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(ColabSurface)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = ColabDarkGreen
            )
        }
    }
}
