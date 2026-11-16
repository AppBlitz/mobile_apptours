package co.edu.uniquindio.apptours.features.user.profile.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import co.edu.uniquindio.apptours.R
import co.edu.uniquindio.apptours.domain.model.BadgeId

@StringRes
internal fun BadgeId.titleRes(): Int = when (this) {
    BadgeId.FIRST_PLACE -> R.string.badge_first_place
    BadgeId.FIRST_COMMENT -> R.string.badge_first_comment
    BadgeId.COMMENTATOR -> R.string.badge_commentator
    BadgeId.TEN_VERIFIED -> R.string.badge_ten_verified
    BadgeId.EXPLORER -> R.string.level_explorer
    BadgeId.ADVENTURER -> R.string.level_adventurer
    BadgeId.AMBASSADOR -> R.string.level_local_ambassador
    BadgeId.FEATURED_OF_MONTH -> R.string.badge_featured_month
    BadgeId.TRAVELER -> R.string.badge_traveler
    BadgeId.GASTRONOMY_LOVER -> R.string.badge_gastronomy_lover
    BadgeId.CULTURE_LOVER -> R.string.badge_culture_lover
    BadgeId.NATURE_LOVER -> R.string.badge_nature_lover
    BadgeId.ENTERTAINMENT_LOVER -> R.string.badge_entertainment_lover
    BadgeId.HISTORY_LOVER -> R.string.badge_history_lover
}

/** Descripción con un %1$d para la meta (target). */
@StringRes
internal fun BadgeId.descriptionRes(): Int = when (this) {
    BadgeId.FIRST_PLACE -> R.string.badge_first_place_desc
    BadgeId.FIRST_COMMENT -> R.string.badge_first_comment_desc
    BadgeId.COMMENTATOR -> R.string.badge_commentator_desc
    BadgeId.TEN_VERIFIED -> R.string.badge_ten_verified_desc
    BadgeId.EXPLORER, BadgeId.ADVENTURER, BadgeId.AMBASSADOR -> R.string.badge_level_desc
    BadgeId.FEATURED_OF_MONTH -> R.string.badge_featured_month_desc
    BadgeId.TRAVELER -> R.string.badge_traveler_desc
    BadgeId.GASTRONOMY_LOVER -> R.string.badge_gastronomy_lover_desc
    BadgeId.CULTURE_LOVER -> R.string.badge_culture_lover_desc
    BadgeId.NATURE_LOVER -> R.string.badge_nature_lover_desc
    BadgeId.ENTERTAINMENT_LOVER -> R.string.badge_entertainment_lover_desc
    BadgeId.HISTORY_LOVER -> R.string.badge_history_lover_desc
}

internal fun BadgeId.icon(): ImageVector = when (this) {
    BadgeId.FIRST_PLACE -> Icons.Filled.AddLocationAlt
    BadgeId.FIRST_COMMENT, BadgeId.COMMENTATOR -> Icons.Filled.Forum
    BadgeId.TEN_VERIFIED -> Icons.Filled.Verified
    BadgeId.EXPLORER, BadgeId.ADVENTURER, BadgeId.AMBASSADOR -> Icons.Filled.MilitaryTech
    BadgeId.FEATURED_OF_MONTH -> Icons.Filled.Star
    BadgeId.TRAVELER -> Icons.Filled.Hiking
    BadgeId.GASTRONOMY_LOVER -> Icons.Filled.Restaurant
    BadgeId.CULTURE_LOVER -> Icons.Filled.TheaterComedy
    BadgeId.NATURE_LOVER -> Icons.Filled.Park
    BadgeId.ENTERTAINMENT_LOVER -> Icons.Filled.Celebration
    BadgeId.HISTORY_LOVER -> Icons.Filled.AccountBalance
}
