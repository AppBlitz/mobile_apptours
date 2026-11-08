package co.edu.uniquindio.apptours.features.user.placedetail

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
import co.edu.uniquindio.apptours.features.user.placedetail.components.*

@Composable
fun PlaceDetailRoute(
    onNavigateBack: () -> Unit,
    onEditPlace: (String) -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlaceDetailScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onEditPlace = { state.place?.let { onEditPlace(it.id) } },
        onToggleInterest = viewModel::onToggleInterest,
        onToggleVisited = viewModel::onToggleVisited,
        onMarkResolved = viewModel::onMarkResolved,
        onCommentTextChange = viewModel::onCommentTextChange,
        onSendComment = viewModel::onSendComment,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun PlaceDetailScreen(
    state: PlaceDetailUiState,
    onNavigateBack: () -> Unit,
    onEditPlace: () -> Unit,
    onToggleInterest: () -> Unit,
    onToggleVisited: () -> Unit,
    onMarkResolved: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onSendComment: () -> Unit,
    onRetry: () -> Unit
) {
    val place = state.place
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (place == null) {
        if (state.isLoading) {
            LoadingOverlay(modifier = Modifier.background(ColabBackground))
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.error_place_not_found))
            }
        }
        return
    }

    val distanceKm = DistanceCalculator.distanceKm(
        state.userLatitude,
        state.userLongitude,
        place.latitude,
        place.longitude
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            PlaceDetailHeader(photosCount = place.photosCount, onNavigateBack = onNavigateBack)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                PlaceDetailInfo(place = place, distanceKm = distanceKm)
                PlaceDetailActions(
                    place = place,
                    isInterested = state.isInterested,
                    isVisited = state.isVisited,
                    isOwner = state.isOwner,
                    onToggleInterest = onToggleInterest,
                    onToggleVisited = onToggleVisited,
                    onEditPlace = onEditPlace,
                    onMarkResolved = onMarkResolved
                )
                PlaceAboutAndLocation(place = place)
                CommentsHeaderAndInput(
                    commentsCount = state.comments.size,
                    commentText = state.commentText,
                    onCommentTextChange = onCommentTextChange,
                    onSendComment = onSendComment
                )
                if (state.errorRes != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
                }
            }
        }

        items(state.comments, key = { it.id }) { comment ->
            CommentCard(comment = comment)
        }
    }
}
