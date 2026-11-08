package co.edu.uniquindio.apptours.features.user.home

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
import co.edu.uniquindio.apptours.features.user.home.components.UserBottomBar
import co.edu.uniquindio.apptours.features.user.feed.FeedRoute
import co.edu.uniquindio.apptours.features.user.notifications.NotificationsRoute
import co.edu.uniquindio.apptours.features.user.profile.ProfileRoute

@Composable
fun UserHomeRoute(
    onOpenPlaceDetail: (String) -> Unit,
    onOpenCreatePlace: () -> Unit,
    onOpenMyPlaces: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: UserHomeViewModel = hiltViewModel()
) {
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    var currentTab by rememberSaveable { mutableStateOf(UserBottomTab.INICIO) }

    Scaffold(
        containerColor = ColabBackground,
        bottomBar = {
            UserBottomBar(
                selectedTab = currentTab,
                unreadCount = unreadCount,
                onSelectTab = { currentTab = it },
                onPublishClick = onOpenCreatePlace
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                UserBottomTab.INICIO, UserBottomTab.MAPA -> FeedRoute(
                    isMapMode = currentTab == UserBottomTab.MAPA,
                    onShowList = { currentTab = UserBottomTab.INICIO },
                    onShowMap = { currentTab = UserBottomTab.MAPA },
                    onOpenNotifications = { currentTab = UserBottomTab.ALERTAS },
                    onOpenPlaceDetail = onOpenPlaceDetail
                )
                UserBottomTab.ALERTAS -> NotificationsRoute(onOpenPlaceDetail = onOpenPlaceDetail)
                UserBottomTab.PERFIL -> ProfileRoute(
                    onOpenMyPlaces = onOpenMyPlaces,
                    onOpenAccountSettings = onOpenAccountSettings,
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}
