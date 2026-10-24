package co.edu.uniquindio.apptours.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.components.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.User

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (User) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
                    value = uiState.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    label = "Nombre completo",
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Filled.Person,
                    errorMessage = uiState.fullNameError
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Correo electrónico",
                    placeholder = "tu@correo.com",
                    leadingIcon = Icons.Filled.Email,
                    errorMessage = uiState.emailError
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Contraseña",
                    placeholder = "Mínimo 8 caracteres",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = uiState.passwordVisible,
                    onPasswordVisibilityChange = viewModel::togglePasswordVisibility,
                    errorMessage = uiState.passwordError
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = "Confirmar contraseña",
                    placeholder = "Repite tu contraseña",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = uiState.passwordVisible,
                    onPasswordVisibilityChange = viewModel::togglePasswordVisibility,
                    errorMessage = uiState.confirmPasswordError
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
                            text = if (uiState.locationPicked) {
                                "Lat ${uiState.latitude}, Lng ${uiState.longitude}"
                            } else {
                                "Elegir ubicación en el..."
                            },
                            color = if (uiState.locationPicked) ColabTextPrimary else ColabTextSecondary,
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
                        checked = uiState.termsAccepted,
                        onCheckedChange = viewModel::onTermsChange,
                        colors = CheckboxDefaults.colors(checkedColor = ColabDarkGreen)
                    )
                    Text(
                        text = "Acepto los términos y la política de privacidad",
                        fontSize = 13.sp,
                        color = ColabTextPrimary
                    )
                }

                if (uiState.termsError != null) {
                    Text(
                        text = uiState.termsError!!,
                        color = ColabError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                if (uiState.registerError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.registerError!!,
                        color = ColabError,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                ColabPrimaryButton(
                    text = "Crear cuenta",
                    onClick = { viewModel.submitRegistration(onRegisterSuccess) }
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
                        selectedLat = uiState.latitude,
                        selectedLng = uiState.longitude,
                        onCoordinatesChanged = { lat, lng ->
                            viewModel.onPickLocationFromMap(lat, lng)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lat ${uiState.latitude} · Lng ${uiState.longitude}",
                        fontWeight = FontWeight.SemiBold,
                        color = ColabDarkGreen,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onPickLocationFromMap(uiState.latitude, uiState.longitude)
                        showMapPickerDialog = false
                    }
                ) {
                    Text("Confirmar punto", color = ColabDarkGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
