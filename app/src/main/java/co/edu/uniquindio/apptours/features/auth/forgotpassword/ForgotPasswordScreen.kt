package co.edu.uniquindio.apptours.features.auth.forgotpassword

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
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

@Composable
fun ForgotPasswordRoute(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ForgotPasswordEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    ForgotPasswordScreen(
        state = state,
        requesters = requesters,
        onEmailChange = viewModel::onEmailChange,
        onEmailFocusLost = viewModel::onEmailFocusLost,
        onSend = viewModel::onSend,
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordUiState,
    requesters: FieldRequesters,
    onEmailChange: (String) -> Unit,
    onEmailFocusLost: () -> Unit,
    onSend: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                value = state.email,
                onValueChange = onEmailChange,
                label = "Correo electrónico",
                placeholder = "tu@correo.com",
                leadingIcon = Icons.Filled.Email,
                errorMessage = state.form.messageOf(FormField.EMAIL),
                fieldRequester = requesters[FormField.EMAIL],
                onFocusLost = onEmailFocusLost
            )

            if (state.errorRes != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ColabErrorBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(state.errorRes),
                        color = ColabError,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (state.sentToEmail != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ColabSageBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.recovery_link_sent, state.sentToEmail),
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
                onClick = onSend,
                enabled = !state.isLoading,
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

        if (state.isLoading) {
            LoadingOverlay(dimBackground = true)
        }
    }
}
