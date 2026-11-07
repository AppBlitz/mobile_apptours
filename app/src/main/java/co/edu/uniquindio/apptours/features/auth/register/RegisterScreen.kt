package co.edu.uniquindio.apptours.features.auth.register

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
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                RegisterEvent.Registered -> onRegisterSuccess()
            }
        }
    }

    RegisterScreen(
        state = state,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onPickLocation = viewModel::onPickLocation,
        onTermsChange = viewModel::onTermsChange,
        onSubmit = viewModel::onSubmit,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onPickLocation: (Double, Double) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var showMapPickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabDarkGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColabLogoBadge()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Únete a la comunidad de exploradores",
                color = ColabSageBg,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(ColabBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                ColabTabToggle(
                    isLeftSelected = false,
                    onTabSelected = { isLogin ->
                        if (isLogin) onNavigateToLogin()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                ColabTextField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    label = "Nombre completo",
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Filled.Person,
                    errorMessage = state.fullNameError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = "Correo electrónico",
                    placeholder = "tu@correo.com",
                    leadingIcon = Icons.Filled.Email,
                    errorMessage = state.emailError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    placeholder = "Mínimo 8 caracteres",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onPasswordVisibilityChange = onTogglePasswordVisibility,
                    errorMessage = state.passwordError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirmar contraseña",
                    placeholder = "Repite tu contraseña",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onPasswordVisibilityChange = onTogglePasswordVisibility,
                    errorMessage = state.confirmPasswordError?.let { stringResource(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tu dirección",
                    color = ColabTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ColabSurface)
                        .border(1.dp, ColabGrayBorder, RoundedCornerShape(14.dp))
                        .clickable { showMapPickerDialog = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = ColabTextSecondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.locationPicked) {
                                "Lat ${state.latitude}, Lng ${state.longitude}"
                            } else {
                                "Elegir ubicación en el..."
                            },
                            color = if (state.locationPicked) ColabTextPrimary else ColabTextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ColabSageBg,
                        modifier = Modifier.clickable { showMapPickerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = null,
                                tint = ColabDarkGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mapa",
                                color = ColabDarkGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Guardamos latitud y longitud al elegir el punto en el mapa.",
                    fontSize = 12.sp,
                    color = ColabTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = onTermsChange,
                        colors = CheckboxDefaults.colors(checkedColor = ColabDarkGreen)
                    )
                    Text(
                        text = "Acepto los términos y la política de privacidad",
                        fontSize = 13.sp,
                        color = ColabTextPrimary
                    )
                }

                if (state.termsError != null) {
                    Text(
                        text = stringResource(state.termsError),
                        color = ColabError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                if (state.registerError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(state.registerError),
                        color = ColabError,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                ColabPrimaryButton(
                    text = "Crear cuenta",
                    onClick = onSubmit,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showMapPickerDialog) {
        AlertDialog(
            onDismissRequest = { showMapPickerDialog = false },
            title = { Text("Fijar tu dirección en el mapa", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Toca el mapa para actualizar tus coordenadas de referencia:",
                        fontSize = 13.sp,
                        color = ColabTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SimulatedInteractiveMap(
                        places = emptyList(),
                        selectedPlace = null,
                        onPlaceSelected = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        interactivePointSelection = true,
                        selectedLat = state.latitude,
                        selectedLng = state.longitude,
                        onCoordinatesChanged = { lat, lng ->
                            onPickLocation(lat, lng)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lat ${state.latitude} · Lng ${state.longitude}",
                        fontWeight = FontWeight.SemiBold,
                        color = ColabDarkGreen,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPickLocation(state.latitude, state.longitude)
                        showMapPickerDialog = false
                    }
                ) {
                    Text("Confirmar punto", color = ColabDarkGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
