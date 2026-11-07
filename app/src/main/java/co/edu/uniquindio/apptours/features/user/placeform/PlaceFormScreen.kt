package co.edu.uniquindio.apptours.features.user.placeform

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
import co.edu.uniquindio.apptours.features.user.placeform.components.*

@Composable
fun PlaceFormRoute(
    onNavigateBack: () -> Unit,
    onPlaceDeleted: () -> Unit,
    viewModel: PlaceFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PlaceFormEvent.Saved -> onNavigateBack()
                PlaceFormEvent.Deleted -> onPlaceDeleted()
            }
        }
    }

    PlaceFormScreen(
        state = state,
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
        onSubmit = viewModel::onSubmit,
        onDelete = viewModel::onDelete
    )
}

@Composable
fun PlaceFormScreen(
    state: PlaceFormUiState,
    onClose: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCategorySelected: (PlaceCategory) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriceRangeSelected: (PriceRange) -> Unit,
    onToggleDay: (String) -> Unit,
    onOpenTimeChange: (String) -> Unit,
    onCloseTimeChange: (String) -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onLocationChange: (Double, Double) -> Unit,
    onSubmit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        PlaceFormHeader(isEditing = state.isEditing, onClose = onClose)

        Spacer(modifier = Modifier.height(18.dp))

        ColabTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = "Título",
            placeholder = "Ej. Café con terraza y vista"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategorySelector(category = state.category, onCategorySelected = onCategorySelected)

        Spacer(modifier = Modifier.height(16.dp))

        DescriptionField(description = state.description, onDescriptionChange = onDescriptionChange)

        Spacer(modifier = Modifier.height(16.dp))

        PriceRangeSelector(priceRange = state.priceRange, onPriceRangeSelected = onPriceRangeSelected)

        Spacer(modifier = Modifier.height(16.dp))

        ScheduleSelector(
            selectedDays = state.selectedDays,
            onToggleDay = onToggleDay,
            openTime = state.openTime,
            onOpenTimeChange = onOpenTimeChange,
            closeTime = state.closeTime,
            onCloseTimeChange = onCloseTimeChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        PhotoPickerSection(
            photosCount = state.photosCount,
            onAddPhoto = onAddPhoto,
            onRemovePhoto = onRemovePhoto
        )

        Spacer(modifier = Modifier.height(18.dp))

        LocationPickerSection(
            latitude = state.latitude,
            longitude = state.longitude,
            onLocationChange = onLocationChange
        )

        if (state.errorRes != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        ColabPrimaryButton(
            text = "Enviar a verificación",
            leadingIcon = Icons.AutoMirrored.Filled.Send,
            enabled = !state.isSaving,
            onClick = onSubmit
        )

        if (state.isEditing) {
            Spacer(modifier = Modifier.height(12.dp))
            ColabOutlinedButton(
                text = "Eliminar publicación",
                borderColor = ColabError,
                contentColor = ColabError,
                leadingIcon = Icons.Outlined.Delete,
                onClick = onDelete
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
