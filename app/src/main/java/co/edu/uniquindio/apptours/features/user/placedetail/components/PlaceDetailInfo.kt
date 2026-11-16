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
import androidx.compose.ui.res.pluralStringResource
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

/** Categoría, estado, título, autor y tarjetas de precio, horario, distancia y relevancia. */
@Composable
internal fun PlaceDetailInfo(
    place: Place,
    distanceKm: Double
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryBadgeChip(category = place.category)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ColabSageBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = ColabDarkGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(place.status.labelRes()),
                    color = ColabDarkGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = place.title,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(34.dp),
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
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.detail_published_by),
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
        Text(
            text = place.authorName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColabTextPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ColabSageBg
        ) {
            Text(
                text = stringResource(place.authorLevel.labelRes()),
                color = ColabDarkGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Cuadrícula de 4 tarjetas: Precio, Horario, Distancia, Relevancia
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailMetricCard(
            title = stringResource(R.string.detail_price_title),
            value = stringResource(place.priceRange.labelRes()),
            icon = Icons.Outlined.Payments,
            modifier = Modifier.weight(1f)
        )
        DetailMetricCard(
            title = stringResource(R.string.detail_schedule_title),
            value = place.scheduleLabel(),
            icon = Icons.Outlined.Schedule,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailMetricCard(
            title = stringResource(R.string.detail_distance_title),
            value = stringResource(R.string.detail_distance_value, Formatters.formatDistance(distanceKm)),
            icon = Icons.Outlined.Place,
            modifier = Modifier.weight(1f)
        )
        DetailMetricCard(
            title = stringResource(R.string.detail_relevance_title),
            value = relevanceLabel(place.votesCount),
            icon = Icons.Outlined.FavoriteBorder,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    DetailMetricCard(
        title = stringResource(R.string.place_detail_visits_title),
        value = pluralStringResource(R.plurals.place_detail_visits_count, place.visitsCount, place.visitsCount),
        icon = Icons.Outlined.Hiking,
        modifier = Modifier.fillMaxWidth()
    )

}
