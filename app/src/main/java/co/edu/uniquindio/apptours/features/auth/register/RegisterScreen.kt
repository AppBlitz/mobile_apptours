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
import co.edu.uniquindio.apptours.core.auth.GoogleTokenResult
import co.edu.uniquindio.apptours.core.auth.rememberGoogleSignInLauncher
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.formField
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*

@Composable
fun RegisterRoute(
    onRegisterSuccess: (UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
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
                is RegisterEvent.Registered -> onRegisterSuccess(event.role)
                is RegisterEvent.ScrollToField -> requesters.reveal(event.field)
                RegisterEvent.LaunchGoogleSignIn -> launchGoogleSignIn()
            }
        }
    }

    RegisterScreen(
        state = state,
        requesters = requesters,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onFieldFocusLost = viewModel::onFieldFocusLost,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onPickLocation = viewModel::onPickLocation,
        onTermsChange = viewModel::onTermsChange,
        onSubmit = viewModel::onSubmit,
        onGoogleSignIn = viewModel::onGoogleClick,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    requesters: FieldRequesters,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onFieldFocusLost: (FormField) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onPickLocation: (Double, Double) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var showMapPickerDialog by rememberSaveable { mutableStateOf(false) }

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
                text = stringResource(R.string.register_tagline),
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
                    label = stringResource(R.string.field_full_name_label),
                    placeholder = stringResource(R.string.field_full_name_placeholder),
                    leadingIcon = Icons.Filled.Person,
                    errorMessage = state.form.messageOf(FormField.FULL_NAME),
                    fieldRequester = requesters[FormField.FULL_NAME],
                    onFocusLost = { onFieldFocusLost(FormField.FULL_NAME) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.field_email_label),
                    placeholder = stringResource(R.string.field_email_placeholder),
                    leadingIcon = Icons.Filled.Email,
                    errorMessage = state.form.messageOf(FormField.EMAIL),
                    fieldRequester = requesters[FormField.EMAIL],
                    onFocusLost = { onFieldFocusLost(FormField.EMAIL) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.field_password_label),
                    placeholder = stringResource(R.string.field_password_new_placeholder),
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onPasswordVisibilityChange = onTogglePasswordVisibility,
                    errorMessage = state.form.messageOf(FormField.PASSWORD),
                    fieldRequester = requesters[FormField.PASSWORD],
                    onFocusLost = { onFieldFocusLost(FormField.PASSWORD) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                ColabTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = stringResource(R.string.field_password_confirm_label),
                    placeholder = stringResource(R.string.field_password_confirm_placeholder),
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onPasswordVisibilityChange = onTogglePasswordVisibility,
                    errorMessage = state.form.messageOf(FormField.PASSWORD_CONFIRMATION),
                    fieldRequester = requesters[FormField.PASSWORD_CONFIRMATION],
                    onFocusLost = { onFieldFocusLost(FormField.PASSWORD_CONFIRMATION) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.map_picker_title_address),
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
                                stringResource(R.string.map_coordinates, state.latitude, state.longitude)
                            } else {
                                stringResource(R.string.register_address_pending)
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
                                text = stringResource(R.string.feed_mode_map),
                                color = ColabDarkGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.register_address_hint),
                    fontSize = 12.sp,
                    color = ColabTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                val termsError = state.form.messageOf(FormField.TERMS)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .formField(requesters[FormField.TERMS])
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = onTermsChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = ColabDarkGreen,
                            uncheckedColor = if (termsError != null) ColabError else ColabTextSecondary
                        )
                    )
                    Text(
                        text = stringResource(R.string.register_terms),
                        fontSize = 13.sp,
                        color = if (termsError != null) ColabError else ColabTextPrimary
                    )
                }

                if (termsError != null) {
                    Text(
                        text = termsError,
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
                    text = stringResource(R.string.auth_create_account),
                    onClick = onSubmit,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(18.dp))

                AuthAlternativeDivider()

                Spacer(modifier = Modifier.height(12.dp))

                GoogleSignInButton(
                    onClick = onGoogleSignIn,
                    enabled = !state.isLoading,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = stringResource(R.string.auth_google_terms_hint),
                    color = ColabTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (state.isLoading) {
                LoadingOverlay(dimBackground = true)
            }
        }
    }

    if (showMapPickerDialog) {
        MapLocationPickerDialog(
            title = stringResource(R.string.map_picker_title_address),
            initialLatitude = state.latitude.takeIf { state.locationPicked },
            initialLongitude = state.longitude.takeIf { state.locationPicked },
            onConfirm = { lat, lng ->
                onPickLocation(lat, lng)
                showMapPickerDialog = false
            },
            onDismiss = { showMapPickerDialog = false }
        )
    }
}
