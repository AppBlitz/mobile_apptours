package co.edu.uniquindio.apptours.features.moderator.review

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
import co.edu.uniquindio.apptours.features.moderator.review.components.*

@Composable
fun ModeratorReviewRoute(
    onNavigateBack: () -> Unit,
    viewModel: ModeratorReviewViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ModeratorReviewEvent.Done -> onNavigateBack()
            }
        }
    }

    ModeratorReviewScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onCheckLocationChange = viewModel::onCheckLocationChange,
        onCheckScheduleChange = viewModel::onCheckScheduleChange,
        onCheckPhotosChange = viewModel::onCheckPhotosChange,
        onVerify = viewModel::onVerify,
        onMarkResolved = viewModel::onMarkResolved,
        onOpenReject = viewModel::onOpenReject,
        onDismissReject = viewModel::onDismissReject,
        onReasonChange = viewModel::onReasonChange,
        onConfirmReject = viewModel::onConfirmReject
    )
}

@Composable
fun ModeratorReviewScreen(
    state: ModeratorReviewUiState,
    onNavigateBack: () -> Unit,
    onCheckLocationChange: (Boolean) -> Unit,
    onCheckScheduleChange: (Boolean) -> Unit,
    onCheckPhotosChange: (Boolean) -> Unit,
    onVerify: () -> Unit,
    onMarkResolved: () -> Unit,
    onOpenReject: () -> Unit,
    onDismissReject: () -> Unit,
    onReasonChange: (String) -> Unit,
    onConfirmReject: () -> Unit
) {
    val place = state.place
    if (place == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                CircularProgressIndicator(color = ColabDarkGreen)
            } else {
                Text(stringResource(R.string.error_place_not_found))
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColabBackground)
            .verticalScroll(rememberScrollState())
    ) {
        ReviewPlaceHeader(onNavigateBack = onNavigateBack)

        Column(modifier = Modifier.padding(20.dp)) {
            ReviewPlaceInfo(place = place)

            Spacer(modifier = Modifier.height(18.dp))

            // Lista de verificación del moderador
            Text(
                text = "Lista de verificación",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            ChecklistItemRow(
                text = "El lugar existe en la ubicación indicada",
                checked = state.checkLocationExists,
                onCheckedChange = onCheckLocationChange
            )
            ChecklistItemRow(
                text = "Horario y precio son plausibles",
                checked = state.checkSchedulePlausible,
                onCheckedChange = onCheckScheduleChange
            )
            ChecklistItemRow(
                text = "Las fotos corresponden al sitio",
                checked = state.checkPhotosMatch,
                onCheckedChange = onCheckPhotosChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Comentarios (${state.comments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ColabDarkGreen
            )
            if (state.comments.isEmpty()) {
                Text(
                    text = "Aún no hay comentarios en esta publicación.",
                    fontSize = 13.sp,
                    color = ColabTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Botones de acción de Moderación (Página 17 del PDF)
            ColabPrimaryButton(
                text = "Verificar / Aprobar",
                leadingIcon = Icons.Filled.Check,
                onClick = onVerify
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ColabOutlinedButton(
                    text = "Marcar como resuelto",
                    onClick = onMarkResolved,
                    modifier = Modifier.weight(1f)
                )
                ColabOutlinedButton(
                    text = "Rechazar",
                    borderColor = ColabError,
                    contentColor = ColabError,
                    leadingIcon = Icons.Filled.Close,
                    onClick = onOpenReject,
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.errorRes != null && !state.showRejectSheet) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stringResource(state.errorRes), color = ColabError, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (state.showRejectSheet) {
        RejectReasonSheet(
            place = place,
            reason = state.rejectionReason,
            errorRes = state.errorRes,
            enabled = !state.isProcessing,
            onReasonChange = onReasonChange,
            onDismiss = onDismissReject,
            onConfirm = onConfirmReject
        )
    }
}
