package co.edu.uniquindio.apptours.features.principal

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.components.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.*

enum class UserBottomTab { INICIO, MAPA, ALERTAS, PERFIL }

@Composable
fun UserMainScreen(
    viewModel: PrincipalViewModel,
    onOpenPlaceDetail: (String) -> Unit,
    onOpenCreatePlace: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var currentTab by remember { mutableStateOf(UserBottomTab.INICIO) }

    val unreadCount = notifications.count {
        !it.isRead && (currentUser == null || it.userId == currentUser?.id)
    }

    Scaffold(
        containerColor = ColabBackground,
        bottomBar = {
            UserBottomBar(
                selectedTab = currentTab,
                unreadCount = unreadCount,
                onSelectTab = { tab ->
                    currentTab = tab
                    if (tab == UserBottomTab.MAPA) {
                        viewModel.setFeedViewMode(FeedViewMode.MAPA)
                    } else if (tab == UserBottomTab.INICIO && uiState.feedViewMode == FeedViewMode.MAPA) {
                        viewModel.setFeedViewMode(FeedViewMode.LISTA)
                    }
                },
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
                UserBottomTab.INICIO, UserBottomTab.MAPA -> {
                    UserFeedAndMapContent(
                        viewModel = viewModel,
                        onOpenPlaceDetail = onOpenPlaceDetail,
                        onSwitchBottomTab = { currentTab = it }
                    )
                }
                UserBottomTab.ALERTAS -> {
                    NotificationsContent(
                        viewModel = viewModel,
                        onOpenPlaceDetail = onOpenPlaceDetail
                    )
                }
                UserBottomTab.PERFIL -> {
                    ProfileContent(
                        viewModel = viewModel,
                        onOpenAccountSettings = onOpenAccountSettings,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun UserBottomBar(
    selectedTab: UserBottomTab,
    unreadCount: Int,
    onSelectTab: (UserBottomTab) -> Unit,
    onPublishClick: () -> Unit
) {
    Surface(
        color = ColabSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Inicio",
                icon = Icons.Outlined.Home,
                selected = selectedTab == UserBottomTab.INICIO,
                onClick = { onSelectTab(UserBottomTab.INICIO) }
            )
            BottomNavItem(
                label = "Mapa",
                icon = Icons.Outlined.Map,
                selected = selectedTab == UserBottomTab.MAPA,
                onClick = { onSelectTab(UserBottomTab.MAPA) }
            )

            // Botón central dorado (+) para Publicar un lugar
            Surface(
                modifier = Modifier
                    .size(54.dp)
                    .clickable { onPublishClick() },
                shape = RoundedCornerShape(18.dp),
                color = ColabBrown,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Publicar un lugar",
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box {
                BottomNavItem(
                    label = "Alertas",
                    icon = Icons.Outlined.Notifications,
                    selected = selectedTab == UserBottomTab.ALERTAS,
                    onClick = { onSelectTab(UserBottomTab.ALERTAS) }
                )
                if (unreadCount > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp),
                        shape = CircleShape,
                        color = ColabBrown
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            BottomNavItem(
                label = "Perfil",
                icon = Icons.Outlined.Person,
                selected = selectedTab == UserBottomTab.PERFIL,
                onClick = { onSelectTab(UserBottomTab.PERFIL) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selected) ColabSageBg else Color.Transparent
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) ColabDarkGreen else ColabTextSecondary,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .size(22.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) ColabDarkGreen else ColabTextSecondary
        )
    }
}

@Composable
private fun UserFeedAndMapContent(
    viewModel: PrincipalViewModel,
    onOpenPlaceDetail: (String) -> Unit,
    onSwitchBottomTab: (UserBottomTab) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val places by viewModel.places.collectAsState()

    val filteredPlaces = remember(uiState, places, currentUser) {
        viewModel.getPublicFilteredPlaces()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera verde oscuro solo en modo Lista (como en Página 8 del PDF)
        if (uiState.feedViewMode == FeedViewMode.LISTA) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ColabDarkGreen)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = ColabLightGreen.copy(alpha = 0.25f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Terrain,
                                contentDescription = null,
                                tint = ColabLightGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hola, ${currentUser?.fullName?.substringBefore(" ") ?: "Valentina"}",
                            color = ColabSageBg,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Descubre lugares",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { onSwitchBottomTab(UserBottomTab.ALERTAS) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ColabMediumGreen.copy(alpha = 0.45f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White
                    )
                }
            }
        }

        // Barra de búsqueda, selector Lista/Mapa y chips de categorías
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColabBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Buscar lugares o categorías", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ColabSurface,
                        unfocusedContainerColor = ColabSurface,
                        unfocusedBorderColor = ColabGrayBorder,
                        focusedBorderColor = ColabDarkGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { viewModel.toggleOnlyNearby() },
                    shape = RoundedCornerShape(16.dp),
                    color = ColabBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Filtrar cercanos",
                            tint = ColabDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selector Lista / Mapa + Chip Cercanos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = ColabSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        val isList = uiState.feedViewMode == FeedViewMode.LISTA
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isList) ColabDarkGreen else Color.Transparent)
                                .clickable {
                                    viewModel.setFeedViewMode(FeedViewMode.LISTA)
                                    onSwitchBottomTab(UserBottomTab.INICIO)
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FormatListBulleted,
                                contentDescription = null,
                                tint = if (isList) Color.White else ColabTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lista",
                                color = if (isList) Color.White else ColabTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (!isList) ColabDarkGreen else Color.Transparent)
                                .clickable {
                                    viewModel.setFeedViewMode(FeedViewMode.MAPA)
                                    onSwitchBottomTab(UserBottomTab.MAPA)
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = null,
                                tint = if (!isList) Color.White else ColabTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mapa",
                                color = if (!isList) Color.White else ColabTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                FilterChip(
                    selected = uiState.onlyNearby,
                    onClick = { viewModel.toggleOnlyNearby() },
                    label = {
                        Text(
                            text = if (uiState.onlyNearby) "Cercanos" else "En la ciudad",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chips de categorías horizontales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryFilterChip(
                    text = "Todos",
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.onCategorySelected(null) }
                )
                PlaceCategory.entries.forEach { category ->
                    CategoryFilterChip(
                        text = category.displayName,
                        icon = categoryIcon(category),
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                }
            }
        }

        if (uiState.feedViewMode == FeedViewMode.LISTA) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cerca de ti",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColabTextPrimary
                        )
                        Text(
                            text = "Ordenado por relevancia",
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                }

                items(filteredPlaces, key = { it.id }) { place ->
                    PlaceFeedCard(
                        place = place,
                        currentUserLat = currentUser?.latitude ?: 4.5350,
                        currentUserLng = currentUser?.longitude ?: -75.6750,
                        isInterested = currentUser?.interestedPlaceIds?.contains(place.id) == true,
                        isVisited = currentUser?.visitedPlaceIds?.contains(place.id) == true,
                        onToggleInterest = { viewModel.toggleInterest(place.id) },
                        onToggleVisited = { viewModel.toggleVisited(place.id) },
                        onClick = { onOpenPlaceDetail(place.id) }
                    )
                }
            }
        } else {
            // Vista de Mapa con tarjeta flotante inferior (Página 9 del PDF)
            val selectedPlace = filteredPlaces.find { it.id == uiState.selectedMapPlaceId }
                ?: filteredPlaces.firstOrNull()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                SimulatedInteractiveMap(
                    places = filteredPlaces,
                    selectedPlace = selectedPlace,
                    onPlaceSelected = { viewModel.selectMapPlace(it.id) },
                    modifier = Modifier.fillMaxSize()
                )

                if (selectedPlace != null) {
                    val dist = Validators.calculateDistanceKm(
                        currentUser?.latitude ?: 4.5350,
                        currentUser?.longitude ?: -75.6750,
                        selectedPlace.latitude,
                        selectedPlace.longitude
                    )
                    val isInterested = currentUser?.interestedPlaceIds?.contains(selectedPlace.id) == true
                    val isVisited = currentUser?.visitedPlaceIds?.contains(selectedPlace.id) == true

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
                                            text = Validators.formatDistance(dist),
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
                                        text = "${selectedPlace.priceRange.displayName} · ${selectedPlace.openTime} – ${selectedPlace.closeTime}",
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
                                    onClick = { viewModel.toggleInterest(selectedPlace.id) },
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
                                    onClick = { viewModel.toggleVisited(selectedPlace.id) },
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
    }
}

@Composable
private fun CategoryFilterChip(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) ColabDarkGreen else ColabSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) ColabDarkGreen else ColabGrayBorder
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else ColabDarkGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else ColabTextPrimary
            )
        }
    }
}

@Composable
fun PlaceDetailScreen(
    placeId: String,
    viewModel: PrincipalViewModel,
    onNavigateBack: () -> Unit,
    onEditPlace: (String) -> Unit
) {
    val places by viewModel.places.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val place = places.find { it.id == placeId }
    if (place == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Publicación no encontrada")
        }
        return
    }

    val placeComments = comments.filter { it.placeId == placeId }
    var newCommentText by remember { mutableStateOf("") }
    var directionToast by remember { mutableStateOf(false) }

    val distanceKm = Validators.calculateDistanceKm(
        currentUser?.latitude ?: 4.5350,
        currentUser?.longitude ?: -75.6750,
        place.latitude,
        place.longitude
    )
    val isInterested = currentUser?.interestedPlaceIds?.contains(place.id) == true
    val isVisited = currentUser?.visitedPlaceIds?.contains(place.id) == true
    val isOwner = currentUser?.id == place.authorId

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Galería superior con botón atrás y contador 1/4 (Página 10 del PDF)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                MountainIllustrationHeader(modifier = Modifier.fillMaxSize(), showPins = false)

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ColabSurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = ColabDarkGreen
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = ColabDarkGreen.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "1 / ${place.photosCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                text = place.status.displayName,
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
                        text = "Publicado por ",
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
                            text = place.authorLevel.displayName,
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
                        title = "PRECIO",
                        value = place.priceRange.displayName,
                        icon = Icons.Outlined.Payments,
                        modifier = Modifier.weight(1f)
                    )
                    DetailMetricCard(
                        title = "HORARIO SUGERIDO",
                        value = place.schedule,
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
                        title = "DISTANCIA",
                        value = "${Validators.formatDistance(distanceKm)} de ti",
                        icon = Icons.Outlined.Place,
                        modifier = Modifier.weight(1f)
                    )
                    DetailMetricCard(
                        title = "RELEVANCIA",
                        value = Validators.relevanceLabel(place.votesCount),
                        icon = Icons.Outlined.FavoriteBorder,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de interacción Me interesa y Marcar como visitado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.toggleInterest(place.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isInterested) ColabAmberBg else ColabSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = ColabBrown,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Me interesa · ${place.votesCount}",
                            color = ColabTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.toggleVisited(place.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isVisited) ColabSageBg else ColabSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircleOutline,
                            contentDescription = null,
                            tint = ColabDarkGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVisited) "Visitado" else "Marcar como visitado",
                            color = ColabDarkGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Opciones de gestión para el autor de la publicación (Editar / Finalizar / Eliminar)
                if (isOwner) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditPlace(place.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Editar", fontSize = 12.sp)
                        }
                        if (place.status != PlaceStatus.RESUELTA) {
                            OutlinedButton(
                                onClick = { viewModel.markPlaceAsResolved(place.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Finalizar", fontSize = 12.sp)
                            }
                        }
                    }
                }

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

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Comentarios y experiencias (${placeComments.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Caja para agregar nuevo comentario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Comparte tu experiencia (+5 pts)...", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ColabSurface,
                            unfocusedContainerColor = ColabSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                viewModel.addComment(place.id, newCommentText)
                                newCommentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColabDarkGreen)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar comentario",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        items(placeComments, key = { it.id }) { comment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColabSurface)
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = ColabSageBg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = comment.authorName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = ColabDarkGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.authorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ColabTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ColabSageBg
                                ) {
                                    Text(
                                        text = comment.authorLevel.displayName,
                                        fontSize = 10.sp,
                                        color = ColabDarkGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = comment.timeAgo,
                                fontSize = 11.sp,
                                color = ColabTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = comment.text,
                            fontSize = 13.sp,
                            color = ColabTextPrimary,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ColabBrown,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = ColabTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColabTextPrimary
            )
        }
    }
}

@Composable
fun CreateEditPlaceScreen(
    placeId: String?,
    viewModel: PrincipalViewModel,
    onNavigateBack: () -> Unit
) {
    val existingPlace = remember(placeId) {
        placeId?.let { viewModel.getPlaceById(it) }
    }

    var title by remember { mutableStateOf(existingPlace?.title ?: "") }
    var category by remember { mutableStateOf(existingPlace?.category ?: PlaceCategory.GASTRONOMIA) }
    var description by remember { mutableStateOf(existingPlace?.description ?: "") }
    var priceRange by remember { mutableStateOf(existingPlace?.priceRange ?: PriceRange.ECONOMICO) }
    var selectedDays by remember {
        mutableStateOf(existingPlace?.daysActive ?: listOf("L", "M", "X", "J", "V", "S"))
    }
    var openTime by remember { mutableStateOf(existingPlace?.openTime ?: "7:00 a. m.") }
    var closeTime by remember { mutableStateOf(existingPlace?.closeTime ?: "7:00 p. m.") }
    var photosCount by remember { mutableStateOf(existingPlace?.photosCount ?: 2) }
    var latitude by remember { mutableStateOf(existingPlace?.latitude ?: 4.5350) }
    var longitude by remember { mutableStateOf(existingPlace?.longitude ?: -75.6750) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Barra superior (Página 11 del PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = ColabDarkGreen
                )
            }
            Text(
                text = if (existingPlace == null) "Publicar un lugar" else "Editar publicación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Aviso de Pendiente de verificación
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ColabAmberBg
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = ColabBrown,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Se creará como «Pendiente de verificación»",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ColabTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Un moderador comprobará que el lugar exista y que los datos sean precisos antes de mostrarlo en el feed público.",
                        fontSize = 12.sp,
                        color = ColabTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        ColabTextField(
            value = title,
            onValueChange = { title = it; errorMessage = null },
            label = "Título",
            placeholder = "Ej. Café con terraza y vista"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Categoría
        Text(
            text = "Categoría",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = ColabTextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColabGrayBorder, RoundedCornerShape(14.dp))
                    .clickable { expandedCategoryMenu = true },
                shape = RoundedCornerShape(14.dp),
                color = ColabSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = categoryIcon(category),
                            contentDescription = null,
                            tint = ColabBrown,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = category.displayName,
                            fontWeight = FontWeight.SemiBold,
                            color = ColabTextPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ColabTextSecondary
                    )
                }
            }

            DropdownMenu(
                expanded = expandedCategoryMenu,
                onDismissRequest = { expandedCategoryMenu = false }
            ) {
                PlaceCategory.entries.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.displayName) },
                        onClick = {
                            category = cat
                            expandedCategoryMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción con contador 0 / 500
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Descripción",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Text(
                text = "${description.length} / 500",
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        ColabTextField(
            value = description,
            onValueChange = { if (it.length <= 500) description = it; errorMessage = null },
            label = "",
            placeholder = "Cuenta qué hace especial este lugar, cómo llegar y qué recomiendas.",
            singleLine = false,
            minLines = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rango de precio
        Text(
            text = "Rango de precio",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = ColabTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PriceRange.entries.forEach { range ->
                val isSelected = priceRange == range
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) ColabDarkGreen else ColabSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder),
                    modifier = Modifier.clickable { priceRange = range }
                ) {
                    Text(
                        text = range.displayName,
                        color = if (isSelected) Color.White else ColabTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horario de atención sugerido
        Text(
            text = "Horario de atención sugerido",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = ColabTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                val active = selectedDays.contains(day)
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            selectedDays = if (active) selectedDays - day else selectedDays + day
                        },
                    shape = CircleShape,
                    color = if (active) ColabDarkGreen else ColabSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = day,
                            color = if (active) Color.White else ColabTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColabTextField(
                value = openTime,
                onValueChange = { openTime = it },
                label = "",
                placeholder = "7:00 a. m.",
                leadingIcon = Icons.Outlined.Schedule,
                modifier = Modifier.weight(1f)
            )
            ColabTextField(
                value = closeTime,
                onValueChange = { closeTime = it },
                label = "",
                placeholder = "7:00 p. m.",
                leadingIcon = Icons.Outlined.Schedule,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Fotos (Simulación visual Cloudinary idéntica al mockup)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Fotos",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Text(
                text = "Mínimo 1 · máximo 6",
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = ColabSurface
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = ColabSageBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Upload,
                            contentDescription = null,
                            tint = ColabDarkGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { if (photosCount < 6) photosCount++ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColabBrown)
                ) {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subir fotos a Cloudinary", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "JPG o PNG · se guardan en tu almacenamiento externo ($photosCount seleccionadas)",
                    fontSize = 11.sp,
                    color = ColabTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(photosCount.coerceAtMost(3)) {
                Box(modifier = Modifier.size(84.dp)) {
                    MountainIllustrationHeader(modifier = Modifier.fillMaxSize())
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .clickable { if (photosCount > 1) photosCount-- },
                        shape = CircleShape,
                        color = ColabDarkGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Quitar foto",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Ubicación en el mapa
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ubicación en el mapa",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Text(
                text = "Toca para fijar el punto",
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        SimulatedInteractiveMap(
            places = emptyList(),
            selectedPlace = null,
            onPlaceSelected = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            interactivePointSelection = true,
            selectedLat = latitude,
            selectedLng = longitude,
            onCoordinatesChanged = { lat, lng ->
                latitude = lat
                longitude = lng
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = ColabSageBg
            ) {
                Text(
                    text = "Lat $latitude",
                    color = ColabDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = ColabSageBg
            ) {
                Text(
                    text = "Lng $longitude",
                    color = ColabDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage!!, color = ColabError, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        ColabPrimaryButton(
            text = "Enviar a verificación",
            leadingIcon = Icons.AutoMirrored.Filled.Send,
            onClick = {
                if (title.isBlank() || description.isBlank()) {
                    errorMessage = "Completa el título y la descripción del lugar."
                } else {
                    viewModel.savePlace(
                        placeId = existingPlace?.id,
                        title = title,
                        category = category,
                        description = description,
                        priceRange = priceRange,
                        daysActive = selectedDays,
                        openTime = openTime,
                        closeTime = closeTime,
                        latitude = latitude,
                        longitude = longitude,
                        photosCount = photosCount
                    )
                    onNavigateBack()
                }
            }
        )

        if (existingPlace != null) {
            Spacer(modifier = Modifier.height(12.dp))
            ColabOutlinedButton(
                text = "Eliminar publicación",
                borderColor = ColabError,
                contentColor = ColabError,
                leadingIcon = Icons.Outlined.Delete,
                onClick = {
                    viewModel.deletePlace(existingPlace.id)
                    onNavigateBack()
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun NotificationsContent(
    viewModel: PrincipalViewModel,
    onOpenPlaceDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val userNotifs = notifications.filter {
        currentUser == null || it.userId == currentUser?.id
    }
    val unreadCount = userNotifs.count { !it.isRead }

    val filtered = when (uiState.notificationFilter) {
        NotificationFilter.TODAS -> userNotifs
        NotificationFilter.NO_LEIDAS -> userNotifs.filter { !it.isRead }
        NotificationFilter.LOGROS -> userNotifs.filter { it.type == NotificationType.BADGE }
    }

    val todayList = filtered.filter { it.isToday }
    val earlierList = filtered.filter { !it.isToday }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .padding(20.dp)
    ) {
        // Cabecera de Notificaciones (Página 12 del PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notificaciones",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                Text(
                    text = "$unreadCount sin leer",
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
            }
            TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                Text(
                    text = "Marcar todas como\nleídas",
                    color = ColabDarkGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryFilterChip(
                text = "Todas",
                selected = uiState.notificationFilter == NotificationFilter.TODAS,
                onClick = { viewModel.setNotificationFilter(NotificationFilter.TODAS) }
            )
            CategoryFilterChip(
                text = "No leídas · $unreadCount",
                selected = uiState.notificationFilter == NotificationFilter.NO_LEIDAS,
                onClick = { viewModel.setNotificationFilter(NotificationFilter.NO_LEIDAS) }
            )
            CategoryFilterChip(
                text = "Logros",
                selected = uiState.notificationFilter == NotificationFilter.LOGROS,
                onClick = { viewModel.setNotificationFilter(NotificationFilter.LOGROS) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (todayList.isNotEmpty()) {
                item {
                    Text(
                        text = "HOY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabTextSecondary
                    )
                }
                items(todayList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationRead(notif.id)
                            notif.relatedPlaceId?.let { onOpenPlaceDetail(it) }
                        }
                    )
                }
            }

            if (earlierList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ANTERIORES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabTextSecondary
                    )
                }
                items(earlierList, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationRead(notif.id)
                            notif.relatedPlaceId?.let { onOpenPlaceDetail(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val iconAndColor = when (notification.type) {
        NotificationType.VOTE -> Pair(Icons.Filled.Favorite, ColabBrown)
        NotificationType.COMMENT -> Pair(Icons.Filled.ChatBubbleOutline, ColabDarkGreen)
        NotificationType.VERIFIED -> Pair(Icons.Filled.Verified, ColabMediumGreen)
        NotificationType.REJECTED -> Pair(Icons.Filled.Close, ColabError)
        NotificationType.BADGE -> Pair(Icons.Filled.EmojiEvents, ColabBrown)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) ColabSurface else ColabCardSurface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = iconAndColor.second.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconAndColor.first,
                        contentDescription = null,
                        tint = iconAndColor.second,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notification.body,
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.timeAgo,
                    fontSize = 11.sp,
                    color = ColabTextSecondary
                )
            }
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ColabBrown)
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    viewModel: PrincipalViewModel,
    onOpenAccountSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val places by viewModel.places.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val points = currentUser?.points ?: 640
    val currentLevel = Validators.calculateLevel(points)
    val nextLevelInfo = Validators.pointsToNextLevel(points)
    val badges = remember(points, places) { viewModel.getUserBadges() }
    val selectedBadge = badges.find { it.id == uiState.selectedBadgeId } ?: badges.first()

    val userPlaces = places.filter { it.authorId == currentUser?.id }
    val activeCount = userPlaces.count { it.status == PlaceStatus.VERIFICADA }.coerceAtLeast(8)
    val finishedCount = userPlaces.count { it.status == PlaceStatus.RESUELTA }.coerceAtLeast(3)
    val pendingCount = userPlaces.count { it.status == PlaceStatus.PENDIENTE }.coerceAtLeast(2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera verde oscuro de Mi Perfil y Gamificación (Página 13 del PDF)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColabDarkGreen)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi perfil",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onOpenAccountSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Ajustes de cuenta",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = ColabSageBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = ColabBrown
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = currentUser?.initials ?: "VR",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColabDarkGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = currentUser?.fullName ?: "Valentina Ríos",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ColabLightGreen.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = currentLevel.displayName,
                            color = ColabSageBg,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Barra de puntos y niveles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$points puntos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${nextLevelInfo.first} para ${nextLevelInfo.second}",
                    color = ColabSageBg,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (points / 1000f).coerceIn(0.1f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ColabBrown,
                trackColor = ColabMediumGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Niveles de Usuario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserLevel.entries.forEach { lvl ->
                    val reached = points >= lvl.minPoints
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            color = if (reached) ColabBrown else ColabMediumGreen
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = lvl.step.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lvl.displayName,
                            color = if (reached) Color.White else ColabSageBg.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Mis estadísticas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox("Publicaciones\nactivas", activeCount.toString(), Modifier.weight(1f))
                StatBox("Finalizadas", finishedCount.toString(), Modifier.weight(1f))
                StatBox("Pendientes de\nverificación", pendingCount.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = "Lugares visitados",
                    value = (currentUser?.visitedPlaceIds?.size?.plus(12) ?: 14).toString(),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Votos recibidos",
                    value = "126",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Insignias (3 de 6 ganadas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Insignias",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                Text(
                    text = "${badges.count { it.unlocked }} de ${badges.size} ganadas",
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cuadrícula 2x3 de Insignias
            badges.chunked(3).forEach { rowBadges ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowBadges.forEach { badge ->
                        val isSelected = badge.id == selectedBadge.id
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectBadge(badge.id) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (badge.unlocked) ColabSurface else ColabCardSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) ColabDarkGreen else ColabGrayBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = CircleShape,
                                    color = if (badge.unlocked) ColabAmberBg else ColabGrayBorder
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (badge.unlocked) Icons.Filled.EmojiEvents else Icons.Outlined.Lock,
                                            contentDescription = null,
                                            tint = if (badge.unlocked) ColabBrown else ColabTextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = badge.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColabTextPrimary,
                                    maxLines = 2
                                )
                                if (!badge.unlocked && badge.progressText != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = badge.progressText,
                                        fontSize = 10.sp,
                                        color = ColabTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detalle de la insignia seleccionada
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ColabSageBg.copy(alpha = 0.65f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = selectedBadge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ColabDarkGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedBadge.description,
                        fontSize = 13.sp,
                        color = ColabTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedBadge.obtainedDate ?: "Progreso: ${selectedBadge.progressText ?: "En curso"}",
                        fontSize = 11.sp,
                        color = ColabTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cómo sumar puntos
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ColabSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cómo sumar puntos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = ColabDarkGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PointRuleRow("Publicar", "+20")
                    PointRuleRow("Comentar", "+5")
                    PointRuleRow("Publicación verificada", "+50")
                    PointRuleRow("Recibir voto", "+2")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ColabOutlinedButton(
                text = "Ajustes de cuenta",
                leadingIcon = Icons.Outlined.Settings,
                onClick = onOpenAccountSettings
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = ColabTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun PointRuleRow(action: String, points: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = action, fontSize = 13.sp, color = ColabTextPrimary)
        Text(text = points, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColabDarkGreen)
    }
}

@Composable
fun AccountSettingsScreen(
    viewModel: PrincipalViewModel,
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var fullName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "Valentina Ríos") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "valentina@correo.com") }
    var latitude by remember(currentUser) { mutableStateOf(currentUser?.latitude ?: 4.5350) }
    var longitude by remember(currentUser) { mutableStateOf(currentUser?.longitude ?: -75.6750) }
    var savedBanner by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera Ajustes de cuenta (Página 14 del PDF)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = ColabDarkGreen
                )
            }
            Text(
                text = "Ajustes de cuenta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar VR con icono de cámara
        Box(modifier = Modifier.size(92.dp)) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = ColabBrown,
                border = androidx.compose.foundation.BorderStroke(2.dp, ColabLightGreen)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser?.initials ?: "VR",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabDarkGreen
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp),
                shape = CircleShape,
                color = ColabDarkGreen,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Cambiar foto",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        TextButton(onClick = {}) {
            Text("Cambiar foto", color = ColabDarkGreen, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        ColabTextField(
            value = fullName,
            onValueChange = { fullName = it; savedBanner = false },
            label = "Nombre completo",
            placeholder = "Valentina Ríos"
        )

        Spacer(modifier = Modifier.height(14.dp))

        ColabTextField(
            value = email,
            onValueChange = { email = it; savedBanner = false },
            label = "Correo electrónico",
            placeholder = "valentina@correo.com"
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Dirección",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ColabGrayBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                color = ColabSurface
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Punto elegido en el mapa",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ColabTextPrimary
                        )
                        Text(
                            text = "Lat $latitude · Lng $longitude",
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }
                    TextButton(
                        onClick = {
                            latitude = ((latitude + 0.0015) * 10000).toInt() / 10000.0
                            longitude = ((longitude - 0.0012) * 10000).toInt() / 10000.0
                        }
                    ) {
                        Text("Cambiar", color = ColabDarkGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (savedBanner) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = ColabSageBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cambios guardados correctamente.",
                    color = ColabDarkGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        ColabPrimaryButton(
            text = "Guardar cambios",
            onClick = {
                viewModel.updateAccount(fullName, email, latitude, longitude)
                savedBanner = true
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Zona de peligro (Eliminar cuenta)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColabError.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = ColabErrorBg
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = ColabError
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zona de peligro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColabError
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Eliminar tu cuenta borra tu perfil, tus puntos e insignias. Esta acción no se puede deshacer.",
                    fontSize = 13.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ColabError),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Eliminar tu cuenta?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se borrarán tus datos, puntos e insignias en memoria y volverás al inicio de sesión.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteAccount()
                        onAccountDeleted()
                    }
                ) {
                    Text("Sí, eliminar", color = ColabError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = ColabTextSecondary)
                }
            }
        )
    }
}
