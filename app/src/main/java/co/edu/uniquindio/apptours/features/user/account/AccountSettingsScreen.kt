package co.edu.uniquindio.apptours.features.user.account

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

@Composable
fun AccountSettingsRoute(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                AccountSettingsEvent.AccountDeleted -> onAccountDeleted()
            }
        }
    }

    AccountSettingsScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onChangeLocation = viewModel::onChangeLocation,
        onSave = viewModel::onSave,
        onRequestDelete = viewModel::onRequestDelete,
        onDismissDelete = viewModel::onDismissDelete,
        onConfirmDelete = viewModel::onConfirmDelete,
        onRetry = viewModel::onRetry
    )
}

@Composable
fun AccountSettingsScreen(
    state: AccountSettingsUiState,
    onNavigateBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onChangeLocation: () -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onRetry: () -> Unit
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    if (!state.isLoaded) {
        LoadingOverlay(modifier = Modifier.background(ColabBackground))
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    border = BorderStroke(2.dp, ColabLightGreen)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = state.initials,
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
                    border = BorderStroke(2.dp, Color.White)
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
                value = state.fullName,
                onValueChange = onFullNameChange,
                label = "Nombre completo",
                placeholder = "Valentina Ríos"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ColabTextField(
                value = state.email,
                onValueChange = onEmailChange,
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
                                text = "Lat ${state.latitude} · Lng ${state.longitude}",
                                fontSize = 12.sp,
                                color = ColabTextSecondary
                            )
                        }
                        TextButton(
                            onClick = onChangeLocation
                        ) {
                            Text("Cambiar", color = ColabDarkGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (state.errorRes != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
            }

            if (state.savedBanner) {
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
                onClick = onSave
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
                        onClick = onRequestDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = ColabError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (state.isSaving) {
            LoadingOverlay(dimBackground = true)
        }
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("¿Eliminar tu cuenta?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Se borrarán tus datos, puntos e insignias y volverás al inicio de sesión.")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete
                ) {
                    Text("Sí, eliminar", color = ColabError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDelete) {
                    Text("Cancelar", color = ColabTextSecondary)
                }
            }
        )
    }
}
