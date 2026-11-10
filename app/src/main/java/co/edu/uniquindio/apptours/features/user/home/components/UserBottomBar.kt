package co.edu.uniquindio.apptours.features.user.home.components

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
import co.edu.uniquindio.apptours.features.user.home.UserBottomTab

@Composable
internal fun UserBottomBar(
    selectedTab: UserBottomTab,
    unreadCount: Int,
    onSelectTab: (UserBottomTab) -> Unit,
    onPublishClick: () -> Unit
) {
    Surface(
        color = ColabSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = stringResource(R.string.tab_home),
                icon = Icons.Outlined.Home,
                selected = selectedTab == UserBottomTab.INICIO,
                onClick = { onSelectTab(UserBottomTab.INICIO) }
            )
            BottomNavItem(
                label = stringResource(R.string.feed_mode_map),
                icon = Icons.Outlined.Map,
                selected = selectedTab == UserBottomTab.MAPA,
                onClick = { onSelectTab(UserBottomTab.MAPA) }
            )

            // Botón central dorado (+) para Publicar un lugar
            Surface(
                modifier = Modifier
                    .size(54.dp)
                    .clickable { onPublishClick() },
                shape = RoundedCornerShape(18.dp),
                color = ColabBrown,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.place_form_header_create),
                        tint = ColabDarkGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Box {
                BottomNavItem(
                    label = stringResource(R.string.tab_alerts),
                    icon = Icons.Outlined.Notifications,
                    selected = selectedTab == UserBottomTab.ALERTAS,
                    onClick = { onSelectTab(UserBottomTab.ALERTAS) }
                )
                if (unreadCount > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp),
                        shape = CircleShape,
                        color = ColabBrown
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            BottomNavItem(
                label = stringResource(R.string.tab_profile),
                icon = Icons.Outlined.Person,
                selected = selectedTab == UserBottomTab.PERFIL,
                onClick = { onSelectTab(UserBottomTab.PERFIL) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selected) ColabSageBg else Color.Transparent
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) ColabDarkGreen else ColabTextSecondary,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .size(22.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) ColabDarkGreen else ColabTextSecondary
        )
    }
}
