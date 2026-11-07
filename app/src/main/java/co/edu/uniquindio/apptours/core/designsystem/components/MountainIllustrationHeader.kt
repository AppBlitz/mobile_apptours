package co.edu.uniquindio.apptours.core.designsystem.components

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
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

@Composable
fun MountainIllustrationHeader(
    modifier: Modifier = Modifier,
    showPins: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ColabBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sol dorado
            drawCircle(
                color = Color(0xFFB98350),
                radius = h * 0.14f,
                center = Offset(w * 0.76f, h * 0.26f)
            )

            // Montaña trasera verde claro
            val backMountain = Path().apply {
                moveTo(0f, h * 0.72f)
                lineTo(w * 0.25f, h * 0.28f)
                lineTo(w * 0.55f, h * 0.62f)
                lineTo(w * 0.75f, h * 0.42f)
                lineTo(w, h * 0.70f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(backMountain, color = Color(0xFF7CA956))

            // Montaña media verde bosque
            val midMountain = Path().apply {
                moveTo(0f, h * 0.85f)
                lineTo(w * 0.22f, h * 0.54f)
                lineTo(w * 0.42f, h * 0.76f)
                lineTo(w * 0.68f, h * 0.52f)
                lineTo(w, h * 0.80f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(midMountain, color = Color(0xFF1B5133))

            // Colina frontal verde profundo
            val frontHill = Path().apply {
                moveTo(0f, h * 0.88f)
                quadraticBezierTo(w * 0.35f, h * 0.78f, w * 0.65f, h * 0.88f)
                quadraticBezierTo(w * 0.85f, h * 0.94f, w, h * 0.86f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(frontHill, color = Color(0xFF0F3622))

            if (showPins) {
                // Sendero punteado dorado
                val dots = listOf(
                    Offset(w * 0.15f, h * 0.94f),
                    Offset(w * 0.21f, h * 0.91f),
                    Offset(w * 0.27f, h * 0.88f),
                    Offset(w * 0.33f, h * 0.85f),
                    Offset(w * 0.39f, h * 0.82f),
                    Offset(w * 0.44f, h * 0.79f)
                )
                dots.forEach { pt ->
                    drawCircle(color = Color(0xFFB98350), radius = 5f, center = pt)
                }
            }
        }

        if (showPins) {
            // Pin central de comunidad
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp),
                shape = CircleShape,
                color = ColabLightGreen,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(ColabBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Groups,
                        contentDescription = null,
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Pin Gastronomía (arriba izquierda)
            CategoryBubblePin(
                icon = Icons.Filled.Restaurant,
                bgColor = ColabBrown,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 62.dp)
            )

            // Pin Cultura (abajo izquierda)
            CategoryBubblePin(
                icon = Icons.Filled.AccountBalance,
                bgColor = Color(0xFF6B5E3C),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 18.dp, top = 56.dp)
            )

            // Pin Naturaleza (derecha)
            CategoryBubblePin(
                icon = Icons.Filled.Terrain,
                bgColor = ColabMediumGreen,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 26.dp, top = 28.dp)
            )
        }
    }
}

@Composable
private fun CategoryBubblePin(
    icon: ImageVector,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .padding(3.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
