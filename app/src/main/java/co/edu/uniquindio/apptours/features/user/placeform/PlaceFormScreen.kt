package co.edu.uniquindio.apptours.features.user.placeform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.designsystem.components.*
import co.edu.uniquindio.apptours.core.designsystem.form.FieldRequesters
import co.edu.uniquindio.apptours.core.designsystem.form.rememberFieldRequesters
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.core.util.messageOf
import co.edu.uniquindio.apptours.domain.model.PlaceCategory
import co.edu.uniquindio.apptours.domain.model.PriceRange
import co.edu.uniquindio.apptours.features.user.placeform.components.*

@Composable
fun PlaceFormRoute(
    onNavigateBack: () -> Unit,
    onPlaceDeleted: () -> Unit,
    viewModel: PlaceFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requesters = rememberFieldRequesters()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PlaceFormEvent.Saved -> onNavigateBack()
                PlaceFormEvent.Deleted -> onPlaceDeleted()
                is PlaceFormEvent.ScrollToField -> requesters.reveal(event.field)
            }
        }
    }

    PlaceFormScreen(
        state = state,
        requesters = requesters,
        actions = PlaceFormActions(
            onClose = onNavigateBack,
            onTitleChange = viewModel::onTitleChange,
            onCategorySelected = viewModel::onCategorySelected,
            onDescriptionChange = viewModel::onDescriptionChange,
            onPriceRangeSelected = viewModel::onPriceRangeSelected,
            onToggleDay = viewModel::onToggleDay,
            onOpenTimeChange = viewModel::onOpenTimeChange,
            onCloseTimeChange = viewModel::onCloseTimeChange,
            onAddPhoto = viewModel::onAddPhoto,
            onRemovePhoto = viewModel::onRemovePhoto,
            onLocationChange = viewModel::onLocationChange,
            onFieldFocusLost = viewModel::onFieldFocusLost,
            onSubmit = viewModel::onSubmit,
            onDeleteClick = viewModel::onDeleteClick,
            onConfirmDelete = viewModel::onConfirmDelete,
            onDismissDelete = viewModel::onDismissDelete,
            onRetry = viewModel::onRetry
        )
    )
}

/** Eventos del formulario agrupados para no pasar quince lambdas sueltas. */
data class PlaceFormActions(
    val onClose: () -> Unit,
    val onTitleChange: (String) -> Unit,
    val onCategorySelected: (PlaceCategory) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onPriceRangeSelected: (PriceRange) -> Unit,
    val onToggleDay: (String) -> Unit,
    val onOpenTimeChange: (String) -> Unit,
    val onCloseTimeChange: (String) -> Unit,
    val onAddPhoto: () -> Unit,
    val onRemovePhoto: () -> Unit,
    val onLocationChange: (Double, Double) -> Unit,
    val onFieldFocusLost: (FormField) -> Unit,
    val onSubmit: () -> Unit,
    val onDeleteClick: () -> Unit,
    val onConfirmDelete: () -> Unit,
    val onDismissDelete: () -> Unit,
    val onRetry: () -> Unit
)

@Composable
fun PlaceFormScreen(
    state: PlaceFormUiState,
    requesters: FieldRequesters,
    actions: PlaceFormActions
) {
    if (state.loadErrorRes != null) {
        ErrorMessage(messageRes = state.loadErrorRes, onRetry = actions.onRetry, modifier = Modifier.background(ColabBackground))
        return
    }
    val form = state.form

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ColabBackground)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            PlaceFormHeader(
                isEditing = state.isEditing,
                returnsToReview = state.returnsToReview,
                onClose = actions.onClose
            )

            Spacer(modifier = Modifier.height(18.dp))

            ColabTextField(
                value = state.title,
                onValueChange = actions.onTitleChange,
                label = stringResource(R.string.place_form_title_label),
                placeholder = stringResource(R.string.place_form_title_placeholder),
                errorMessage = form.messageOf(FormField.TITLE),
                fieldRequester = requesters[FormField.TITLE],
                onFocusLost = { actions.onFieldFocusLost(FormField.TITLE) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CategorySelector(
                category = state.category,
                onCategorySelected = actions.onCategorySelected,
                errorMessage = form.messageOf(FormField.CATEGORY),
                requester = requesters[FormField.CATEGORY]
            )

            Spacer(modifier = Modifier.height(16.dp))

            DescriptionField(
                description = state.description,
                onDescriptionChange = actions.onDescriptionChange,
                errorMessage = form.messageOf(FormField.DESCRIPTION),
                requester = requesters[FormField.DESCRIPTION],
                onFocusLost = { actions.onFieldFocusLost(FormField.DESCRIPTION) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PriceRangeSelector(
                priceRange = state.priceRange,
                onPriceRangeSelected = actions.onPriceRangeSelected,
                errorMessage = form.messageOf(FormField.PRICE_RANGE),
                requester = requesters[FormField.PRICE_RANGE]
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScheduleSelector(
                selectedDays = state.selectedDays,
                onToggleDay = actions.onToggleDay,
                openTime = state.openTime,
                onOpenTimeChange = actions.onOpenTimeChange,
                closeTime = state.closeTime,
                onCloseTimeChange = actions.onCloseTimeChange,
                daysError = form.messageOf(FormField.DAYS),
                openTimeError = form.messageOf(FormField.OPEN_TIME),
                closeTimeError = form.messageOf(FormField.CLOSE_TIME),
                daysRequester = requesters[FormField.DAYS],
                openTimeRequester = requesters[FormField.OPEN_TIME],
                closeTimeRequester = requesters[FormField.CLOSE_TIME],
                onOpenTimeFocusLost = { actions.onFieldFocusLost(FormField.OPEN_TIME) },
                onCloseTimeFocusLost = { actions.onFieldFocusLost(FormField.CLOSE_TIME) }
            )

            Spacer(modifier = Modifier.height(18.dp))

            PhotoPickerSection(
                photosCount = state.photosCount,
                onAddPhoto = actions.onAddPhoto,
                onRemovePhoto = actions.onRemovePhoto,
                errorMessage = form.messageOf(FormField.PHOTOS),
                requester = requesters[FormField.PHOTOS]
            )

            Spacer(modifier = Modifier.height(18.dp))

            LocationPickerSection(
                latitude = state.latitude,
                longitude = state.longitude,
                locationPicked = state.locationPicked,
                category = state.category,
                title = state.title,
                onLocationChange = actions.onLocationChange,
                errorMessage = form.messageOf(FormField.LOCATION),
                requester = requesters[FormField.LOCATION]
            )

            // Errores que no son de un campo (sin red, sin permiso…): junto al botón de enviar.
            if (state.errorRes != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            ColabPrimaryButton(
                text = stringResource(R.string.place_form_submit),
                leadingIcon = Icons.AutoMirrored.Filled.Send,
                enabled = !state.isSaving,
                onClick = actions.onSubmit
            )

            if (state.isEditing) {
                Spacer(modifier = Modifier.height(12.dp))
                ColabOutlinedButton(
                    text = stringResource(R.string.place_form_delete),
                    borderColor = ColabError,
                    contentColor = ColabError,
                    leadingIcon = Icons.Outlined.Delete,
                    onClick = actions.onDeleteClick
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        if (state.isLoading || state.isSaving) {
            LoadingOverlay(dimBackground = true)
        }
    }

    if (state.showDeleteDialog) {
        DeletePlaceDialog(onConfirm = actions.onConfirmDelete, onDismiss = actions.onDismissDelete)
    }
}
