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

/** Icono y color de cada categoría (mapa, chips, tarjetas). */
fun categoryIcon(category: PlaceCategory): ImageVector {
    return when (category) {
        PlaceCategory.GASTRONOMIA -> Icons.Filled.Restaurant
        PlaceCategory.CULTURA -> Icons.Filled.AccountBalance
        PlaceCategory.NATURALEZA -> Icons.Filled.Terrain
        PlaceCategory.ENTRETENIMIENTO -> Icons.Filled.TheaterComedy
        PlaceCategory.HISTORIA -> Icons.Filled.HourglassBottom
    }
}

fun categoryColor(category: PlaceCategory): Color {
    return when (category) {
        PlaceCategory.GASTRONOMIA -> ColabBrown
        PlaceCategory.CULTURA -> Color(0xFF6B5E3C)
        PlaceCategory.NATURALEZA -> ColabMediumGreen
        PlaceCategory.ENTRETENIMIENTO -> Color(0xFF8C5A3C)
        PlaceCategory.HISTORIA -> Color(0xFF7A6348)
    }
}
