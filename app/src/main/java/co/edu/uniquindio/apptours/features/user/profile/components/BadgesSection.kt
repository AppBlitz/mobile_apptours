package co.edu.uniquindio.apptours.features.user.profile.components

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
import co.edu.uniquindio.apptours.domain.model.Badge

@Composable
internal fun BadgesSection(
    badges: List<Badge>,
    selectedBadge: Badge,
    onSelectBadge: (String) -> Unit
) {

    // Insignias (3 de 6 ganadas)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Insignias",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
        Text(
            text = "${badges.count { it.unlocked }} de ${badges.size} ganadas",
            fontSize = 13.sp,
            color = ColabTextSecondary
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Cuadrícula 2x3 de Insignias
    badges.chunked(3).forEach { rowBadges ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rowBadges.forEach { badge ->
                val isSelected = badge.id == selectedBadge.id
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectBadge(badge.id) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (badge.unlocked) ColabSurface else ColabCardSurface,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) ColabDarkGreen else ColabGrayBorder
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = if (badge.unlocked) ColabAmberBg else ColabGrayBorder
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (badge.unlocked) Icons.Filled.EmojiEvents else Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (badge.unlocked) ColabBrown else ColabTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = badge.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColabTextPrimary,
                            maxLines = 2
                        )
                        if (!badge.unlocked && badge.progressText != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badge.progressText,
                                fontSize = 10.sp,
                                color = ColabTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Detalle de la insignia seleccionada
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabSageBg.copy(alpha = 0.65f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = selectedBadge.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = selectedBadge.description,
                fontSize = 13.sp,
                color = ColabTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = selectedBadge.obtainedDate ?: "Progreso: ${selectedBadge.progressText ?: "En curso"}",
                fontSize = 11.sp,
                color = ColabTextSecondary
            )
        }
    }

}
