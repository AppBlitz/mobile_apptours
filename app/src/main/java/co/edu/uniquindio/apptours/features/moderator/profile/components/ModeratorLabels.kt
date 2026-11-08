package co.edu.uniquindio.apptours.features.moderator.profile.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.core.theme.ColabBrown
import co.edu.uniquindio.apptours.core.theme.ColabError
import co.edu.uniquindio.apptours.core.theme.ColabMediumGreen
import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModeratorRank
import co.edu.uniquindio.apptours.domain.model.TrophyId

@StringRes
internal fun ModeratorRank.labelRes(): Int = when (this) {
    ModeratorRank.REVISOR -> R.string.rank_reviewer
    ModeratorRank.GUARDIAN -> R.string.rank_guardian
    ModeratorRank.CURADOR -> R.string.rank_curator
    ModeratorRank.EXPERTO -> R.string.rank_expert
}

@StringRes
internal fun ModerationAction.labelRes(): Int = when (this) {
    ModerationAction.VERIFIED -> R.string.xp_verify
    ModerationAction.REJECTED -> R.string.xp_reject
    ModerationAction.RESOLVED -> R.string.xp_resolve
}

internal fun ModerationAction.icon(): ImageVector = when (this) {
    ModerationAction.VERIFIED -> Icons.Filled.CheckCircle
    ModerationAction.REJECTED -> Icons.Filled.Cancel
    ModerationAction.RESOLVED -> Icons.Filled.TaskAlt
}

internal fun ModerationAction.color(): Color = when (this) {
    ModerationAction.VERIFIED -> ColabMediumGreen
    ModerationAction.REJECTED -> ColabError
    ModerationAction.RESOLVED -> ColabBrown
}

@StringRes
internal fun TrophyId.titleRes(): Int = when (this) {
    TrophyId.FIRST_REVIEW -> R.string.trophy_first_review
    TrophyId.REVIEWS_10 -> R.string.trophy_reviews_10
    TrophyId.REVIEWS_50 -> R.string.trophy_reviews_50
    TrophyId.REVIEWS_100 -> R.string.trophy_reviews_100
    TrophyId.FIRST_REJECTION -> R.string.trophy_first_rejection
    TrophyId.CRITICAL_EYE -> R.string.trophy_critical_eye
    TrophyId.QUALITY_SEAL -> R.string.trophy_quality_seal
    TrophyId.INTENSE_DAY -> R.string.trophy_intense_day
    TrophyId.CLOSER -> R.string.trophy_closer
}

/** Descripción con un %1$d para la meta. */
@StringRes
internal fun TrophyId.descriptionRes(): Int = when (this) {
    TrophyId.FIRST_REVIEW, TrophyId.REVIEWS_10, TrophyId.REVIEWS_50, TrophyId.REVIEWS_100 -> R.string.trophy_reviews_desc
    TrophyId.FIRST_REJECTION -> R.string.trophy_first_rejection_desc
    TrophyId.CRITICAL_EYE -> R.string.trophy_critical_eye_desc
    TrophyId.QUALITY_SEAL -> R.string.trophy_quality_seal_desc
    TrophyId.INTENSE_DAY -> R.string.trophy_intense_day_desc
    TrophyId.CLOSER -> R.string.trophy_closer_desc
}

internal fun TrophyId.icon(): ImageVector = when (this) {
    TrophyId.FIRST_REVIEW -> Icons.Filled.Flag
    TrophyId.REVIEWS_10, TrophyId.REVIEWS_50, TrophyId.REVIEWS_100 -> Icons.Filled.EmojiEvents
    TrophyId.FIRST_REJECTION -> Icons.Filled.Block
    TrophyId.CRITICAL_EYE -> Icons.Filled.Visibility
    TrophyId.QUALITY_SEAL -> Icons.Filled.Verified
    TrophyId.INTENSE_DAY -> Icons.Filled.Bolt
    TrophyId.CLOSER -> Icons.Filled.TaskAlt
}
