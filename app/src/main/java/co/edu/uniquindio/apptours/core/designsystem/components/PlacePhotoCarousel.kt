package co.edu.uniquindio.apptours.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen

/**
 * Carrusel de fotos de un lugar con el indicador "1 / N". Se desliza de lado a lado.
 * [overlay] dibuja encima (botón atrás, etiquetas) sin que el carrusel lo tape.
 */
@Composable
fun PlacePhotoCarousel(
    imageUrls: List<String>,
    placeTitle: String,
    modifier: Modifier = Modifier,
    counterAlignment: Alignment = Alignment.BottomEnd,
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val pageCount = imageUrls.size.coerceAtLeast(1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Box(modifier = modifier) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            AsyncPlaceImage(
                url = imageUrls.getOrNull(page),
                contentDescription = stringResource(R.string.photo_of_place, page + 1, placeTitle),
                modifier = Modifier.fillMaxSize()
            )
        }

        if (imageUrls.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(counterAlignment)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = ColabDarkGreen.copy(alpha = 0.85f)
            ) {
                Text(
                    text = stringResource(R.string.photo_counter, pagerState.currentPage + 1, imageUrls.size),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        overlay()
    }
}
