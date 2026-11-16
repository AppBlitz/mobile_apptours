package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.apptours.core.designsystem.components.categoryColor
import co.edu.uniquindio.apptours.core.designsystem.components.categoryIcon
import co.edu.uniquindio.apptours.core.theme.ColabBrown
import co.edu.uniquindio.apptours.domain.model.PlaceCategory

/** Marcador redondo con el icono y el color de la categoría; el seleccionado crece y se pinta café. */
@Composable
fun CategoryMarker(
    category: PlaceCategory,
    selected: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(if (selected) 50.dp else 38.dp),
        shape = CircleShape,
        color = if (selected) ColabBrown else categoryColor(category),
        shadowElevation = if (selected) 6.dp else 2.dp,
        border = BorderStroke(2.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = categoryIcon(category),
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(if (selected) 26.dp else 20.dp)
            )
        }
    }
}

/** Pin fijo en el centro del selector de ubicación. */
@Composable
fun CenterPin(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.Place,
        contentDescription = null,
        tint = ColabBrown,
        modifier = modifier.size(46.dp)
    )
}

/** Punto simple para una ubicación sin categoría. */
@Composable
fun PointMarker(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(24.dp),
        shape = CircleShape,
        color = ColabBrown,
        shadowElevation = 3.dp,
        border = BorderStroke(3.dp, Color.White)
    ) {}
}
