package co.edu.uniquindio.apptours.features.auth.login

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
import co.edu.uniquindio.apptours.core.auth.GoogleTokenResult
import co.edu.uniquindio.apptours.core.auth.rememberGoogleSignInLauncher
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

@Composable
fun LoginRoute(
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()
    val launchGoogleSignIn = rememberGoogleSignInLauncher { result ->
        when (result) {
            is GoogleTokenResult.Success -> viewModel.onGoogleToken(result.idToken)
            is GoogleTokenResult.Failure -> viewModel.onGoogleError(result.messageRes)
            GoogleTokenResult.Cancelled -> Unit
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoggedIn -> onLoginSuccess(event.role)
                is LoginEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    LoginScreen(
        state = state,
        requesters = requesters,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onFieldFocusLost = viewModel::onFieldFocusLost,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSubmit = viewModel::onSubmit,
        onGoogleSignIn = launchGoogleSignIn,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    requesters: FieldRequesters,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onFieldFocusLost: (FormField) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmit: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
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
                text = stringResource(R.string.login_tagline),
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
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.field_email_label),
                    keyboardOptions = ColabKeyboard.Email,
                    placeholder = stringResource(R.string.field_email_placeholder),
                    leadingIcon = Icons.Filled.Email,
                    errorMessage = state.form.messageOf(FormField.EMAIL),
                    fieldRequester = requesters[FormField.EMAIL],
                    onFocusLost = { onFieldFocusLost(FormField.EMAIL) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColabTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.field_password_label),
                    placeholder = stringResource(R.string.field_password_placeholder),
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onPasswordVisibilityChange = onTogglePasswordVisibility,
                    errorMessage = state.form.messageOf(FormField.PASSWORD),
                    fieldRequester = requesters[FormField.PASSWORD],
                    onFocusLost = { onFieldFocusLost(FormField.PASSWORD) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = stringResource(R.string.login_forgot_question),
                        color = ColabTextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = stringResource(R.string.login_recover),
                        color = ColabDarkGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onNavigateToForgotPassword() }
                    )
                }

                if (state.authError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = ColabErrorBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(state.authError),
                            color = ColabError,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                ColabPrimaryButton(
                    text = stringResource(R.string.auth_login),
                    onClick = onSubmit,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(18.dp))

                AuthAlternativeDivider()

                Spacer(modifier = Modifier.height(12.dp))

                GoogleSignInButton(onClick = onGoogleSignIn, enabled = !state.isLoading)

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.login_first_time),
                    color = ColabTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                ColabOutlinedButton(
                    text = stringResource(R.string.auth_create_account),
                    onClick = onNavigateToRegister
                )
            }

            if (state.isLoading) {
                LoadingOverlay(dimBackground = true)
            }
        }
    }
}
