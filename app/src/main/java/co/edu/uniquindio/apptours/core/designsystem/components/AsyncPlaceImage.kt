package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * Foto de un lugar cargada desde la nube (o una foto local recién elegida).
 * Mientras carga, si falla o si el lugar no tiene fotos, se ve la ilustración de montañas de la paleta.
 */
@Composable
fun AsyncPlaceImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    showPins: Boolean = false
) {
    Box(modifier = modifier.clipToBounds()) {
        MountainIllustrationHeader(modifier = Modifier.fillMaxSize(), showPins = showPins)
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
