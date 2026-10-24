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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.components.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus

enum class ModeratorBottomTab { POR_REVISAR, HISTORIAL, CUENTA }

@Composable
fun ModeratorMainScreen(
    viewModel: PrincipalViewModel,
    onOpenReviewDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val places by viewModel.places.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val verifiedToday by viewModel.verifiedTodayCount.collectAsState()
    val rejectedToday by viewModel.rejectedTodayCount.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var currentTab by remember { mutableStateOf(ModeratorBottomTab.POR_REVISAR) }

    val pendingCount = places.count { it.status == PlaceStatus.PENDIENTE }

    Scaffold(
        containerColor = ColabBackground,
        bottomBar = {
            NavigationBar(containerColor = ColabSurface) {
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.POR_REVISAR,
                    onClick = {
                        currentTab = ModeratorBottomTab.POR_REVISAR
                        viewModel.setModeratorFilter(ModeratorFilter.PENDIENTES)
                    },
                    icon = { Icon(Icons.Outlined.AssignmentLate, contentDescription = null) },
                    label = { Text("Por revisar") }
                )
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.HISTORIAL,
                    onClick = {
                        currentTab = ModeratorBottomTab.HISTORIAL
                        viewModel.setModeratorFilter(ModeratorFilter.VERIFICADAS)
                    },
                    icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                    label = { Text("Historial") }
                )
                NavigationBarItem(
                    selected = currentTab == ModeratorBottomTab.CUENTA,
                    onClick = { currentTab = ModeratorBottomTab.CUENTA },
                    icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    label = { Text("Cuenta") }
                )
            }
        }
    ) { innerPadding ->
        if (currentTab == ModeratorBottomTab.CUENTA) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = ColabDarkGreen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = ColabBrown,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = currentUser?.fullName ?: "Moderador coLab",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
                Text(
                    text = currentUser?.email ?: "moderador@colabtour.com",
                    fontSize = 14.sp,
                    color = ColabTextSecondary
                )
                Spacer(modifier = Modifier.height(28.dp))
                ColabPrimaryButton(
                    text = "Cerrar sesión de moderador",
                    onClick = onLogout
                )
            }
        } else {
            val filteredList = when (uiState.moderatorFilter) {
                ModeratorFilter.PENDIENTES -> places.filter { it.status == PlaceStatus.PENDIENTE }
                ModeratorFilter.VERIFICADAS -> places.filter {
                    it.status == PlaceStatus.VERIFICADA || it.status == PlaceStatus.RESUELTA
                }
                ModeratorFilter.RECHAZADAS -> places.filter { it.status == PlaceStatus.RECHAZADA }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Cabecera verde del Panel de moderación (Página 16 del PDF)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("co", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Lab", color = ColabBrown, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(" tour", color = ColabSageBg, fontSize = 16.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ColabBrown.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = ColabBrown,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Modo moderador",
                                    color = ColabSageBg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Panel de moderación",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ModeratorCounterCard(
                            count = pendingCount.toString(),
                            label = "Pendientes",
                            modifier = Modifier.weight(1f)
                        )
                        ModeratorCounterCard(
                            count = verifiedToday.toString(),
                            label = "Verificadas hoy",
                            modifier = Modifier.weight(1f)
                        )
                        ModeratorCounterCard(
                            count = rejectedToday.toString(),
                            label = "Rechazadas hoy",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeratorFilterChip(
                            text = "Pendientes $pendingCount",
                            selected = uiState.moderatorFilter == ModeratorFilter.PENDIENTES,
                            onClick = { viewModel.setModeratorFilter(ModeratorFilter.PENDIENTES) }
                        )
                        ModeratorFilterChip(
                            text = "Verificadas",
                            selected = uiState.moderatorFilter == ModeratorFilter.VERIFICADAS,
                            onClick = { viewModel.setModeratorFilter(ModeratorFilter.VERIFICADAS) }
                        )
                        ModeratorFilterChip(
                            text = "Rechazadas",
                            selected = uiState.moderatorFilter == ModeratorFilter.RECHAZADAS,
                            onClick = { viewModel.setModeratorFilter(ModeratorFilter.RECHAZADAS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cola de revisión",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = ColabDarkGreen
                        )
                        Text(
                            text = "Más antiguas primero",
                            fontSize = 12.sp,
                            color = ColabTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(filteredList, key = { it.id }) { place ->
                            ModeratorQueueCard(
                                place = place,
                                onReviewClick = { onOpenReviewDetail(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeratorCounterCard(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = ColabMediumGreen.copy(alpha = 0.55f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = count,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = ColabSageBg,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ModeratorFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) ColabDarkGreen else ColabSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else ColabTextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ModeratorQueueCard(
    place: Place,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReviewClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ColabSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadgeChip(category = place.category)
                Text(
                    text = place.createdAtLabel,
                    fontSize = 12.sp,
                    color = ColabTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = place.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColabTextPrimary
            )
            Text(
                text = "${place.authorName} · ${place.authorLevel.displayName}",
                fontSize = 12.sp,
                color = ColabTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = place.description,
                fontSize = 13.sp,
                color = ColabTextPrimary,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ColabAmberBg
                ) {
                    Text(
                        text = place.status.displayName,
                        color = ColabBrown,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                TextButton(onClick = onReviewClick) {
                    Text(
                        text = "Revisar >",
                        color = ColabDarkGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorReviewDetailScreen(
    placeId: String,
    viewModel: PrincipalViewModel,
    onNavigateBack: () -> Unit
) {
    val places by viewModel.places.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val place = places.find { it.id == placeId } ?: return
    val placeComments = comments.filter { it.placeId == placeId }

    var checkLocationExists by remember { mutableStateOf(true) }
    var checkSchedulePlausible by remember { mutableStateOf(true) }
    var checkPhotosMatch by remember { mutableStateOf(true) }

    var showRejectModal by remember { mutableStateOf(false) }
    var rejectionReason by remember {
        mutableStateOf("No pudimos verificar que el lugar exista en la ubicación indicada. Agrega una foto de la fachada y corrige las coordenadas.")
    }
    var rejectionError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera ilustrada con etiqueta Pendiente (Página 17 del PDF)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            MountainIllustrationHeader(modifier = Modifier.fillMaxSize())

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
                color = ColabAmberBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = ColabBrown,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pendiente",
                        color = ColabTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
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
                                    text = place.authorLevel.displayName,
                                    color = ColabDarkGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enviada ${place.createdAtLabel}",
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
                        Text(place.priceRange.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
                        Text(place.schedule, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

            Spacer(modifier = Modifier.height(18.dp))

            // Lista de verificación del moderador
            Text(
                text = "Lista de verificación",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChecklistItemRow(
                text = "El lugar existe en la ubicación indicada",
                checked = checkLocationExists,
                onCheckedChange = { checkLocationExists = it }
            )
            ChecklistItemRow(
                text = "Horario y precio son plausibles",
                checked = checkSchedulePlausible,
                onCheckedChange = { checkSchedulePlausible = it }
            )
            ChecklistItemRow(
                text = "Las fotos corresponden al sitio",
                checked = checkPhotosMatch,
                onCheckedChange = { checkPhotosMatch = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Comentarios (${placeComments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ColabDarkGreen
            )
            if (placeComments.isEmpty()) {
                Text(
                    text = "Aún no hay comentarios en esta publicación.",
                    fontSize = 13.sp,
                    color = ColabTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botones de acción de Moderación (Página 17 del PDF)
            ColabPrimaryButton(
                text = "Verificar / Aprobar",
                leadingIcon = Icons.Filled.Check,
                onClick = {
                    viewModel.approvePlace(place.id)
                    onNavigateBack()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ColabOutlinedButton(
                    text = "Marcar como resuelto",
                    onClick = {
                        viewModel.markPlaceAsResolved(place.id)
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f)
                )
                ColabOutlinedButton(
                    text = "Rechazar",
                    borderColor = ColabError,
                    contentColor = ColabError,
                    leadingIcon = Icons.Filled.Close,
                    onClick = { showRejectModal = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Hoja emergente / Modal de Rechazar publicación con motivo obligatorio (Página 18 del PDF)
    if (showRejectModal) {
        ModalBottomSheet(
            onDismissRequest = { showRejectModal = false },
            containerColor = ColabSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rechazar publicación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColabDarkGreen
                    )
                    IconButton(onClick = { showRejectModal = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = "${place.title} · por ${place.authorName}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ColabTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "La publicación dejará de mostrarse en el feed público y el autor verá tu motivo en sus notificaciones.",
                    fontSize = 13.sp,
                    color = ColabTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Motivo del rechazo *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColabTextPrimary
                    )
                    Text(
                        text = "Obligatorio · ${rejectionReason.length} / 300",
                        fontSize = 12.sp,
                        color = ColabTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ColabTextField(
                    value = rejectionReason,
                    onValueChange = {
                        if (it.length <= 300) rejectionReason = it
                        rejectionError = null
                    },
                    label = "",
                    placeholder = "Explica el motivo del rechazo...",
                    singleLine = false,
                    minLines = 3,
                    errorMessage = rejectionError
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chips rápidos de motivo de rechazo (Página 18 del PDF)
                val quickReasons = listOf(
                    "Ubicación incorrecta",
                    "No existe",
                    "Información imprecisa",
                    "Contenido inapropiado"
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickReasons.chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pair.forEach { reasonChip ->
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = ColabCardSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ColabGrayBorder),
                                    modifier = Modifier.clickable {
                                        rejectionReason = "$reasonChip: no pudimos verificar la información enviada."
                                        rejectionError = null
                                    }
                                ) {
                                    Text(
                                        text = reasonChip,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ColabTextPrimary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                ColabPrimaryButton(
                    text = "Confirmar rechazo",
                    containerColor = ColabError,
                    contentColor = Color.White,
                    onClick = {
                        if (rejectionReason.isBlank()) {
                            rejectionError = "Debes escribir un motivo obligatorio para rechazar la publicación"
                        } else {
                            viewModel.rejectPlace(place.id, rejectionReason)
                            showRejectModal = false
                            onNavigateBack()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showRejectModal = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar", color = ColabTextSecondary, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ChecklistItemRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = ColabDarkGreen)
        )
        Text(text = text, fontSize = 14.sp, color = ColabTextPrimary)
    }
}
