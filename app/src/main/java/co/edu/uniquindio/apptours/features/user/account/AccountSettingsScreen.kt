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
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.map.*
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.*
import co.edu.uniquindio.apptours.domain.model.*
import co.edu.uniquindio.apptours.features.user.account.components.LanguageSelector
import co.edu.uniquindio.apptours.features.user.account.components.ProfilePhotoEditor

@Composable
fun AccountSettingsRoute(
    onNavigateBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                AccountSettingsEvent.AccountDeleted -> onAccountDeleted()
                is AccountSettingsEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    AccountSettingsScreen(
        state = state,
        requesters = requesters,
        onNavigateBack = onNavigateBack,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onFieldFocusLost = viewModel::onFieldFocusLost,
        onChangeLocation = viewModel::onChangeLocation,
        onPhotoPicked = viewModel::onPhotoPicked,
        onRemovePhoto = viewModel::onRemovePhoto,
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
    requesters: FieldRequesters,
    onNavigateBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onFieldFocusLost: (FormField) -> Unit,
    onChangeLocation: (Double, Double) -> Unit,
    onPhotoPicked: (String) -> Unit,
    onRemovePhoto: () -> Unit,
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
    var showLocationPicker by rememberSaveable { mutableStateOf(false) }

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
                        contentDescription = stringResource(R.string.action_back),
                        tint = ColabDarkGreen
                    )
                }
                Text(
                    text = stringResource(R.string.account_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColabDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfilePhotoEditor(
                photoUrl = state.photoUrl,
                initials = state.initials,
                uploadProgress = state.photoUploadProgress,
                enabled = !state.isSaving,
                onPhotoPicked = onPhotoPicked,
                onRemovePhoto = onRemovePhoto
            )

            Spacer(modifier = Modifier.height(10.dp))

            ColabTextField(
                value = state.fullName,
                onValueChange = onFullNameChange,
                label = stringResource(R.string.field_full_name_label),
                keyboardOptions = ColabKeyboard.Name,
                placeholder = stringResource(R.string.field_full_name_placeholder),
                errorMessage = state.form.messageOf(FormField.FULL_NAME),
                fieldRequester = requesters[FormField.FULL_NAME],
                onFocusLost = { onFieldFocusLost(FormField.FULL_NAME) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ColabTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.field_email_label),
                keyboardOptions = ColabKeyboard.Email,
                placeholder = stringResource(R.string.field_email_placeholder),
                enabled = state.canChangeEmail,
                errorMessage = state.form.messageOf(FormField.EMAIL),
                fieldRequester = requesters[FormField.EMAIL],
                onFocusLost = { onFieldFocusLost(FormField.EMAIL) }
            )

            if (!state.canChangeEmail) {
                Text(
                    text = stringResource(R.string.profile_email_google_hint),
                    color = ColabTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.account_address),
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
                                text = stringResource(R.string.account_location_point),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = ColabTextPrimary
                            )
                            Text(
                                text = stringResource(R.string.map_coordinates, state.latitude, state.longitude),
                                fontSize = 12.sp,
                                color = ColabTextSecondary
                            )
                        }
                        TextButton(onClick = { showLocationPicker = true }) {
                            Text(
                                stringResource(R.string.map_change_point),
                                color = ColabDarkGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (showLocationPicker) {
                    MapLocationPickerDialog(
                        title = stringResource(R.string.map_picker_title_address),
                        initialLatitude = state.latitude,
                        initialLongitude = state.longitude,
                        onConfirm = { lat, lng ->
                            onChangeLocation(lat, lng)
                            showLocationPicker = false
                        },
                        onDismiss = { showLocationPicker = false }
                    )
                }
            }

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

            if (state.photoSavedBanner) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(color = ColabSageBg, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.account_photo_saved),
                        color = ColabDarkGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (state.savedBanner) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = ColabSageBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(R.string.account_saved),
                            color = ColabDarkGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (state.emailVerificationSentTo != null) {
                            Text(
                                text = stringResource(
                                    R.string.profile_email_verification_sent,
                                    state.emailVerificationSentTo
                                ),
                                color = ColabDarkGreen,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ColabPrimaryButton(
                text = stringResource(R.string.account_save),
                onClick = onSave,
                enabled = !state.isSaving
            )

            Spacer(modifier = Modifier.height(24.dp))

            LanguageSelector()

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
                            text = stringResource(R.string.account_danger_zone),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ColabError
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.account_delete_warning),
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
                        Text(stringResource(R.string.account_delete), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (state.isSaving) {
            LoadingOverlay(dimBackground = true)
        }
    }

    if (state.showDeleteConfirm) {
        ColabConfirmDialog(
            title = stringResource(R.string.account_delete_title),
            message = stringResource(R.string.account_delete_body),
            confirmText = stringResource(R.string.account_delete_confirm),
            dismissText = stringResource(R.string.action_cancel),
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}
