package co.edu.uniquindio.apptours.features.user.placedetail.components

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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

/** Descripción, ubicación exacta con mini-mapa y botón "Cómo llegar". */
@Composable
internal fun PlaceAboutAndLocation(place: Place) {
    var directionToast by remember { mutableStateOf(false) }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Sobre este lugar",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = place.description,
        fontSize = 14.sp,
        color = ColabTextPrimary,
        lineHeight = 21.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Ubicación exacta con coordenadas
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ubicación exacta",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = "${place.latitude}, ${place.longitude}",
            fontSize = 12.sp,
            color = ColabTextSecondary
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    SimulatedInteractiveMap(
        places = listOf(place),
        selectedPlace = place,
        onPlaceSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    ColabOutlinedButton(
        text = if (directionToast) "Ruta trazada hacia (${place.latitude}, ${place.longitude})" else "Cómo llegar",
        onClick = { directionToast = !directionToast },
        leadingIcon = Icons.Filled.NearMe
    )

}
