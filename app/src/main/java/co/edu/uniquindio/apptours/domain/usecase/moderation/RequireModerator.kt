package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase

/** Solo un moderador puede verificar o rechazar publicaciones. */
internal suspend fun GetSessionUseCase.requireModerator(): Result<User> =
    require().mapCatching { user ->
        if (user.role != UserRole.MODERATOR) throw AppException.NotAllowed
        user
    }
