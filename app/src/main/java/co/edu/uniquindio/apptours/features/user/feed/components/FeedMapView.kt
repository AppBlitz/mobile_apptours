package co.edu.uniquindio.apptours.features.user.feed.components

import co.edu.uniquindio.apptours.core.util.formatTime
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.designsystem.components.CategoryBadgeChip
import co.edu.uniquindio.apptours.core.designsystem.components.AsyncPlaceImage
import co.edu.uniquindio.apptours.core.map.MapboxPlacesMap
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.DistanceCalculator
import co.edu.uniquindio.apptours.core.util.Formatters
import co.edu.uniquindio.apptours.core.util.labelRes
import co.edu.uniquindio.apptours.domain.model.Place

/** Mapa real del feed con tarjeta flotante del lugar seleccionado. */
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        MapboxPlacesMap(
            places = places,
            selectedPlaceId = selectedPlace?.id,
            onPlaceSelected = { onSelectPlace(it.id) },
            initialLatitude = userLatitude,
            initialLongitude = userLongitude,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
        )

        if (selectedPlace != null) {
            SelectedPlaceCard(
                place = selectedPlace,
                distanceKm = DistanceCalculator.distanceKm(
                    userLatitude, userLongitude, selectedPlace.latitude, selectedPlace.longitude
                ),
                isInterested = selectedPlace.id in votedPlaceIds,
                isVisited = selectedPlace.id in visitedPlaceIds,
                onToggleInterest = { onToggleInterest(selectedPlace.id) },
                onToggleVisited = { onToggleVisited(selectedPlace.id) },
                onOpenDetail = { onOpenPlaceDetail(selectedPlace.id) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun SelectedPlaceCard(
    place: Place,
    distanceKm: Double,
    isInterested: Boolean,
    isVisited: Boolean,
    onToggleInterest: () -> Unit,
    onToggleVisited: () -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncPlaceImage(
                    url = place.coverImageUrl,
                    contentDescription = place.title,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryBadgeChip(place.category)
                        Text(
                            text = Formatters.formatDistance(distanceKm),
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = place.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColabTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.map_card_price_hours,
                            stringResource(place.priceRange.labelRes()),
                            formatTime(place.openTime),
                            formatTime(place.closeTime)
                        ),
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
                    onClick = onToggleInterest,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isInterested) ColabAmberBg else Color.Transparent
                    )
                ) {
                    Icon(Icons.Filled.Favorite, null, tint = ColabBrown, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.place_interest_count, place.votesCount),
                        fontSize = 12.sp,
                        color = ColabTextPrimary
                    )
                }

                OutlinedButton(
                    onClick = onToggleVisited,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isVisited) ColabSageBg else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.Filled.CheckCircleOutline,
                        null,
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.place_visited), fontSize = 12.sp, color = ColabDarkGreen)
                }

                Button(
                    onClick = onOpenDetail,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColabDarkGreen)
                ) {
                    Text(stringResource(R.string.place_see_detail), fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
