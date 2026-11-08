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

/** Dashboard (U-17) con conteos reales de publicaciones y visitas. */
@Composable
internal fun StatsDashboard(stats: UserStats) {

    Text(
        text = "Mis estadísticas",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ColabDarkGreen
    )
    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBox("Publicaciones\nactivas", stats.activeCount.toString(), Modifier.weight(1f))
        StatBox("Finalizadas", stats.finishedCount.toString(), Modifier.weight(1f))
        StatBox("Pendientes de\nverificación", stats.pendingCount.toString(), Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBox(
            label = "Lugares visitados",
            value = stats.visitedCount.toString(),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            label = "Votos recibidos",
            value = stats.votesReceived.toString(),
            modifier = Modifier.weight(1f)
        )
        StatBox(
            label = stringResource(R.string.stats_rejected),
            value = stats.rejectedCount.toString(),
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.border(1.dp, ColabGrayBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = ColabTextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
