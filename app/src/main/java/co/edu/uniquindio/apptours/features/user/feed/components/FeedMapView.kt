package co.edu.uniquindio.apptours.features.user.feed.components

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

/** Vista de mapa con tarjeta flotante del lugar seleccionado. */
@Composable
internal fun FeedMapView(
    places: List<Place>,
    selectedPlaceId: String?,
    userLatitude: Double,
    userLongitude: Double,
    votedPlaceIds: Set<String>,
    visitedPlaceIds: Set<String>,
    onSelectPlace: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onToggleVisited: (String) -> Unit,
    onOpenPlaceDetail: (String) -> Unit
) {

    val selectedPlace = places.find { it.id == selectedPlaceId }
        ?: places.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        SimulatedInteractiveMap(
            places = places,
            selectedPlace = selectedPlace,
            onPlaceSelected = { onSelectPlace(it.id) },
            modifier = Modifier.fillMaxSize()
        )

        if (selectedPlace != null) {
            val dist = DistanceCalculator.distanceKm(
                userLatitude,
                userLongitude,
                selectedPlace.latitude,
                selectedPlace.longitude
            )
            val isInterested = selectedPlace.id in votedPlaceIds
            val isVisited = selectedPlace.id in visitedPlaceIds

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ColabSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MountainIllustrationHeader(
                            modifier = Modifier.size(84.dp),
                            showPins = false
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryBadgeChip(selectedPlace.category)
                                Text(
                                    text = Formatters.formatDistance(dist),
                                    fontSize = 12.sp,
                                    color = ColabTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selectedPlace.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ColabTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${stringResource(selectedPlace.priceRange.labelRes())} · ${selectedPlace.openTime} – ${selectedPlace.closeTime}",
                                fontSize = 12.sp,
                                color = ColabTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onToggleInterest(selectedPlace.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isInterested) ColabAmberBg else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = ColabBrown,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Me interesa · ${selectedPlace.votesCount}",
                                fontSize = 12.sp,
                                color = ColabTextPrimary
                            )
                        }

                        OutlinedButton(
                            onClick = { onToggleVisited(selectedPlace.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isVisited) ColabSageBg else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircleOutline,
                                contentDescription = null,
                                tint = ColabDarkGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Visitado", fontSize = 12.sp, color = ColabDarkGreen)
                        }

                        Button(
                            onClick = { onOpenPlaceDetail(selectedPlace.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColabDarkGreen)
                        ) {
                            Text("Ver detalle >", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

}
