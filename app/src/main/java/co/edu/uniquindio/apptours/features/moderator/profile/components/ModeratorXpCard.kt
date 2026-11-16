package co.edu.uniquindio.apptours.features.moderator.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.*
import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModeratorRank
import co.edu.uniquindio.apptours.domain.model.ModeratorXp
import co.edu.uniquindio.apptours.domain.usecase.moderation.CalculateModeratorRankUseCase

/** Cabecera verde: nombre, correo, rango, XP y barra hacia el siguiente rango. */
@Composable
internal fun ModeratorXpHeader(
    fullName: String,
    email: String,
    xp: Int,
    rank: CalculateModeratorRankUseCase.RankProgress,
    onLogout: () -> Unit
) {
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
                text = stringResource(R.string.moderator_profile_title),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.moderator_logout),
                    tint = ColabSageBg
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = ColabBrown) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = ColabDarkGreen, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = fullName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = email, color = ColabSageBg, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(10.dp), color = ColabLightGreen.copy(alpha = 0.3f)) {
                    Text(
                        text = stringResource(rank.rank.labelRes()),
                        color = ColabSageBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.moderator_xp, xp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            val next = rank.next
            Text(
                text = if (next != null) {
                    stringResource(R.string.moderator_xp_to_next, rank.xpToNext, stringResource(next.labelRes()))
                } else {
                    stringResource(R.string.moderator_max_rank)
                },
                color = ColabSageBg,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { rank.fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = ColabBrown,
            trackColor = ColabMediumGreen
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ModeratorRank.entries.forEach { r ->
                val reached = xp >= r.minXp
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = if (reached) ColabBrown else ColabMediumGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text((r.ordinal + 1).toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(r.labelRes()),
                        color = if (reached) Color.White else ColabSageBg.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/** Tabla de XP: sale de [ModerationAction] y [ModeratorXp], la misma que se usa al otorgarla. */
@Composable
internal fun ModeratorXpRulesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ColabSurface,
        border = BorderStroke(1.dp, ColabGrayBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.xp_rules_title),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColabDarkGreen
            )
            Spacer(modifier = Modifier.height(10.dp))
            ModerationAction.entries.forEach { action ->
                XpRow(stringResource(action.labelRes()), action.xp)
            }
            XpRow(stringResource(R.string.xp_daily_bonus, ModeratorXp.DAILY_BONUS_REVIEWS), ModeratorXp.DAILY_BONUS)
        }
    }
}

@Composable
private fun XpRow(label: String, xp: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = ColabTextPrimary, modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.xp_value, xp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColabDarkGreen
        )
    }
}
