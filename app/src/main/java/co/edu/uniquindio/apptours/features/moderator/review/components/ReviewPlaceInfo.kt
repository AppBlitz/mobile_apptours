package co.edu.uniquindio.apptours.features.moderator.review.components

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

/** Categoría, título, autor, precio, horario, descripción y ubicación. */
@Composable
internal fun ReviewPlaceInfo(place: Place) {

    CategoryBadgeChip(category = place.category)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = place.title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Tarjeta del autor y tiempo de envío
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = ColabSageBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = place.authorName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ColabDarkGreen
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = place.authorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ColabTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ColabSageBg
                    ) {
                        Text(
                            text = stringResource(place.authorLevel.labelRes()),
                            color = ColabDarkGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enviada ${relativeTimeLabel(place.createdAt)}",
                        fontSize = 12.sp,
                        color = ColabTextSecondary
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = ColabSurface
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("PRECIO", fontSize = 11.sp, color = ColabTextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(place.priceRange.labelRes()), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = ColabSurface
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("HORARIO SUGERIDO", fontSize = 11.sp, color = ColabTextSecondary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(place.scheduleLabel(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
        text = "Descripción",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = place.description,
        fontSize = 14.sp,
        color = ColabTextPrimary,
        lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Ubicación",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = ColabDarkGreen
        )
        Text(
            text = "${place.latitude}, ${place.longitude}",
            fontSize = 12.sp,
            color = ColabTextSecondary
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    SimulatedInteractiveMap(
        places = listOf(place),
        selectedPlace = place,
        onPlaceSelected = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    )

}
