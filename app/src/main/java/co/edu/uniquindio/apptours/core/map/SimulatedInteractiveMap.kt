package co.edu.uniquindio.apptours.core.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke

// TODO(IT-7): reemplazar por MapboxPlacesMap / MapLocationPicker.

@Composable
fun SimulatedInteractiveMap(
    places: List<Place>,
    selectedPlace: Place?,
    onPlaceSelected: (Place) -> Unit,
    modifier: Modifier = Modifier,
    interactivePointSelection: Boolean = false,
    selectedLat: Double = 4.5350,
    selectedLng: Double = -75.6750,
    onCoordinatesChanged: ((Double, Double) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE6ECD9))
            .clickable(enabled = interactivePointSelection) {
                // Alterna ligeramente las coordenadas en Armenia/Quindío para simular el toque en el mapa
                val nextLat = ((selectedLat + 0.0023) * 10000).toInt() / 10000.0
                val nextLng = ((selectedLng - 0.0018) * 10000).toInt() / 10000.0
                onCoordinatesChanged?.invoke(nextLat, nextLng)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Bloques de parques verdes suaves
            drawRect(
                color = Color(0xFFD5E3C8),
                topLeft = Offset(w * 0.05f, h * 0.08f),
                size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.28f)
            )
            drawRect(
                color = Color(0xFFD5E3C8),
                topLeft = Offset(w * 0.62f, h * 0.55f),
                size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.35f)
            )

            // Río curvo azul verdoso
            val riverPath = Path().apply {
                moveTo(0f, h * 0.65f)
                quadraticBezierTo(w * 0.45f, h * 0.50f, w, h * 0.62f)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFFBBD7CC),
                style = Stroke(width = 28f, cap = StrokeCap.Round)
            )

            // Calles de la cuadrícula
            val streetColor = Color(0xFFF7F5EB)
            listOf(0.22f, 0.48f, 0.76f).forEach { yRatio ->
                drawLine(
                    color = streetColor,
                    start = Offset(0f, h * yRatio),
                    end = Offset(w, h * yRatio),
                    strokeWidth = 18f
                )
            }
            listOf(0.25f, 0.56f, 0.82f).forEach { xRatio ->
                drawLine(
                    color = streetColor,
                    start = Offset(w * xRatio, 0f),
                    end = Offset(w * xRatio, h),
                    strokeWidth = 18f
                )
            }
        }

        if (interactivePointSelection) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = ColabDarkGreen
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Toca el mapa para fijar el lugar",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Pin central seleccionado
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(54.dp),
                shape = CircleShape,
                color = ColabBrown.copy(alpha = 0.25f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = ColabBrown
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        } else {
            val positions = listOf(
                Alignment.Center,
                Alignment.TopStart,
                Alignment.TopEnd,
                Alignment.BottomStart,
                Alignment.CenterEnd
            )
            places.forEachIndexed { index, place ->
                val align = positions[index % positions.size]
                val isSelected = selectedPlace?.id == place.id
                val pinPadding = when (index % 5) {
                    0 -> PaddingValues(0.dp)
                    1 -> PaddingValues(start = 52.dp, top = 48.dp)
                    2 -> PaddingValues(end = 56.dp, top = 36.dp)
                    3 -> PaddingValues(start = 36.dp, bottom = 68.dp)
                    else -> PaddingValues(end = 42.dp, top = 70.dp)
                }
                Box(
                    modifier = Modifier
                        .align(align)
                        .padding(pinPadding)
                        .clickable { onPlaceSelected(place) }
                ) {
                    Surface(
                        modifier = Modifier.size(if (isSelected) 54.dp else 40.dp),
                        shape = CircleShape,
                        color = if (isSelected) ColabBrown else categoryColor(place.category),
                        shadowElevation = if (isSelected) 6.dp else 2.dp,
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = categoryIcon(place.category),
                                contentDescription = place.title,
                                tint = Color.White,
                                modifier = Modifier.size(if (isSelected) 26.dp else 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
