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

/** Cabecera verde con avatar, nivel, barra de puntos y los 4 niveles. */
@Composable
internal fun ProfileHeader(
    fullName: String,
    initials: String,
    photoUrl: String,
    points: Int,
    levelProgress: Float,
    onOpenAccountSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val currentLevel = UserLevel.fromPoints(points)
    val nextLevel = currentLevel.next
    val pointsToNextLevel = nextLevel?.let { it.minPoints - points } ?: 0
    val nextLevelName = stringResource(nextLevel?.labelRes() ?: R.string.level_max)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColabDarkGreen)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onOpenAccountSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.account_title),
                        tint = Color.White
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.profile_logout),
                        tint = ColabSageBg
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(
                photoUrl = photoUrl,
                initials = initials,
                fontSize = 22.sp,
                contentDescription = stringResource(R.string.account_photo_description),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = fullName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ColabLightGreen.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = stringResource(currentLevel.labelRes()),
                        color = ColabSageBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Barra de puntos y niveles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.profile_points, points),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = if (nextLevel != null) {
                    stringResource(R.string.profile_points_to_next, pointsToNextLevel, nextLevelName)
                } else {
                    nextLevelName
                },
                color = ColabSageBg,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Avance real dentro del nivel actual (de su mínimo al mínimo del siguiente).
        LinearProgressIndicator(
            progress = { levelProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = ColabBrown,
            trackColor = ColabMediumGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Niveles de Usuario
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UserLevel.entries.forEach { lvl ->
                val reached = points >= lvl.minPoints
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = if (reached) ColabBrown else ColabMediumGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = lvl.step.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(lvl.labelRes()),
                        color = if (reached) Color.White else ColabSageBg.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

}
