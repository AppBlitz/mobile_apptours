package co.edu.uniquindio.apptours.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.core.components.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.User

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabDarkGreen)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra superior con marca y botón Omitir
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = ColabLightGreen.copy(alpha = 0.25f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Terrain,
                            contentDescription = null,
                            tint = ColabLightGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row {
                        Text("co", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Lab", color = ColabBrown, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Text("— tour —", color = ColabSageBg, fontSize = 10.sp)
                }
            }

            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Omitir",
                    color = ColabSageBg,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ilustración central de montañas y pines como en el mockup (Página 3)
        MountainIllustrationHeader(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            showPins = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Descubre lo que las\nguías no cuentan",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Lugares compartidos por viajeros y locales,\nverificados por moderadores para que llegues\nsin sorpresas.",
            color = ColabSageBg.copy(alpha = 0.9f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Indicadores de página del onboarding
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ColabBrown)
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColabSageBg.copy(alpha = 0.4f))
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColabSageBg.copy(alpha = 0.4f))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ColabPrimaryButton(
            text = "Siguiente",
            onClick = onNavigateToLogin,
            containerColor = ColabBrown,
            contentColor = Color.White
        )
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabDarkGreen)
    ) {
        // Cabecera verde oscuro con logo y lema
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColabLogoBadge()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Explora. Comparte. Descubre.",
                color = ColabSageBg,
                fontSize = 14.sp
            )
        }

        // Tarjeta inferior redondeada
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ColabTabToggle(
                    isLeftSelected = true,
                    onTabSelected = { isLogin ->
                        if (!isLogin) onNavigateToRegister()
                    }
                )

                Spacer(modifier = Modifier.height(22.dp))

                ColabTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Correo electrónico",
                    placeholder = "tu@correo.com",
                    leadingIcon = Icons.Filled.Email,
                    errorMessage = uiState.emailError
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColabTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Contraseña",
                    placeholder = "Tu contraseña",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = uiState.passwordVisible,
                    onPasswordVisibilityChange = viewModel::togglePasswordVisibility,
                    errorMessage = uiState.passwordError
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña? ",
                        color = ColabTextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Recuperar",
                        color = ColabDarkGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() }
                    )
                }

                if (uiState.authError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = ColabErrorBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.authError!!,
                            color = ColabError,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                ColabPrimaryButton(
                    text = "Iniciar sesión",
                    onClick = { viewModel.submitLogin(onLoginSuccess) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "¿Primera vez en coLab tour?",
                    color = ColabTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                ColabOutlinedButton(
                    text = "Crear cuenta",
                    onClick = onNavigateToRegister
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Accesos rápidos de demostración para probar Usuario y Moderador con un toque
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ColabCardSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = ColabDarkGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Moderadores: ingresen con las credenciales asignadas",
                                color = ColabTextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AssistChip(
                                onClick = { viewModel.fillDemoCredentials(isModerator = false) },
                                label = { Text("Demo Usuario", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            )
                            AssistChip(
                                onClick = { viewModel.fillDemoCredentials(isModerator = true) },
                                label = { Text("Demo Moderador", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Shield,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: LoginViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ColabCardSurface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = ColabDarkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Icono circular con sobre y candado del mockup (Página 6)
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(ColabSageBg),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ColabDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = ColabBrown,
                    modifier = Modifier.size(34.dp)
                )
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(32.dp),
                shape = CircleShape,
                color = ColabBrown
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ColabTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Escribe el correo de tu cuenta y te enviaremos un enlace para crear una contraseña nueva.",
            fontSize = 14.sp,
            color = ColabTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        ColabTextField(
            value = uiState.recoveryEmail,
            onValueChange = viewModel::onRecoveryEmailChange,
            label = "Correo electrónico",
            placeholder = "tu@correo.com",
            leadingIcon = Icons.Filled.Email,
            errorMessage = uiState.recoveryError
        )

        if (uiState.recoverySuccessMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = ColabSageBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = uiState.recoverySuccessMessage!!,
                    color = ColabDarkGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        ColabPrimaryButton(
            text = "Enviar enlace de recuperación",
            onClick = { viewModel.sendRecoveryLink() },
            leadingIcon = Icons.AutoMirrored.Filled.Send
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColabGrayBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = ColabSurface
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = ColabDarkGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sirve para usuarios y moderadores. Si no lo ves en tu bandeja, revisa la carpeta de spam.",
                    fontSize = 13.sp,
                    color = ColabTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onNavigateBack) {
            Text(
                text = "Volver a iniciar sesión",
                color = ColabDarkGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}
