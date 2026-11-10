package co.edu.uniquindio.apptours.features.user.feed

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
import co.edu.uniquindio.apptours.features.user.feed.components.*

@Composable
fun FeedRoute(
    isMapMode: Boolean,
    onShowList: () -> Unit,
    onShowMap: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPlaceDetail: (String) -> Unit,
    onOpenRecommendations: () -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // "Cercanos" mide desde donde está el teléfono; sin permiso, desde la dirección guardada.
    val requestDeviceLocation = rememberDeviceLocationRequester { result ->
        if (result is DeviceLocationResult.Found) viewModel.onDeviceLocation(result.latitude, result.longitude)
    }
    FeedScreen(
        state = state,
        isMapMode = isMapMode,
        onQueryChange = viewModel::onQueryChange,
        onLocationFilterSelected = { filter ->
            viewModel.onLocationFilterSelected(filter)
            if (filter == LocationFilter.CERCANOS) requestDeviceLocation()
        },
        onCategorySelected = viewModel::onCategorySelected,
        onSelectMapPlace = viewModel::onSelectMapPlace,
        onToggleInterest = viewModel::onToggleInterest,
        onToggleVisited = viewModel::onToggleVisited,
        onShowList = onShowList,
        onShowMap = onShowMap,
        onOpenNotifications = onOpenNotifications,
        onOpenPlaceDetail = onOpenPlaceDetail,
        onOpenRecommendations = onOpenRecommendations,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun FeedScreen(
    state: FeedUiState,
    isMapMode: Boolean,
    onQueryChange: (String) -> Unit,
    onLocationFilterSelected: (LocationFilter) -> Unit,
    onCategorySelected: (PlaceCategory?) -> Unit,
    onSelectMapPlace: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onToggleVisited: (String) -> Unit,
    onShowList: () -> Unit,
    onShowMap: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPlaceDetail: (String) -> Unit,
    onOpenRecommendations: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (state.isLoading) {
        LoadingOverlay(modifier = Modifier.background(ColabBackground))
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera verde oscuro solo en modo Lista
        if (!isMapMode) {
            FeedHeader(firstName = state.firstName, onOpenNotifications = onOpenNotifications)
        }

        FeedFilters(
            query = state.query,
            onQueryChange = onQueryChange,
            location = state.location,
            onLocationFilterSelected = onLocationFilterSelected,
            isMapMode = isMapMode,
            onShowList = onShowList,
            onShowMap = onShowMap,
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        if (state.location == LocationFilter.CERCANOS) {
            Text(
                text = stringResource(
                    if (state.usingDeviceLocation) R.string.location_nearby_device else R.string.location_nearby_saved
                ),
                fontSize = 12.sp,
                color = ColabTextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
            )
        }

        if (!isMapMode) {
            FeedList(
                places = state.places,
                userLatitude = state.userLatitude,
                userLongitude = state.userLongitude,
                votedPlaceIds = state.votedPlaceIds,
                visitedPlaceIds = state.visitedPlaceIds,
                onToggleInterest = onToggleInterest,
                onToggleVisited = onToggleVisited,
                onOpenPlaceDetail = onOpenPlaceDetail,
                topRecommendationTitle = state.topRecommendationTitle,
                recommendationsColdStart = state.recommendationsColdStart,
                onOpenRecommendations = onOpenRecommendations
            )
        } else {
            FeedMapView(
                places = state.places,
                selectedPlaceId = state.selectedMapPlaceId,
                userLatitude = state.userLatitude,
                userLongitude = state.userLongitude,
                votedPlaceIds = state.votedPlaceIds,
                visitedPlaceIds = state.visitedPlaceIds,
                onSelectPlace = onSelectMapPlace,
                onToggleInterest = onToggleInterest,
                onToggleVisited = onToggleVisited,
                onOpenPlaceDetail = onOpenPlaceDetail
            )
        }
    }
}
