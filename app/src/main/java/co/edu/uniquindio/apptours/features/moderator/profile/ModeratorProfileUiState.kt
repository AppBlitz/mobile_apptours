package co.edu.uniquindio.apptours.features.moderator.profile

import androidx.annotation.StringRes
import co.edu.uniquindio.apptours.domain.model.TrophyId
import co.edu.uniquindio.apptours.domain.usecase.moderation.ModeratorProfile

data class ModeratorProfileUiState(
    val profile: ModeratorProfile? = null,
    val selectedTrophy: TrophyId? = null,
    val isLoading: Boolean = true,
    @StringRes val loadErrorRes: Int? = null
)

sealed interface ModeratorProfileEvent {
    data object LoggedOut : ModeratorProfileEvent
}
