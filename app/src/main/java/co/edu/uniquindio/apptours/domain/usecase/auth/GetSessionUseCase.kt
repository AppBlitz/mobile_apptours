package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import javax.inject.Inject

/** Usuario de la sesión actual (lectura puntual), o null si no hay sesión. */
class GetSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? =
        authRepository.currentUserId()?.let { userRepository.getUser(it) }

    /** Igual que [invoke] pero falla con [AppException.NotAuthenticated] si no hay sesión. */
    suspend fun require(): Result<User> =
        invoke()?.let { Result.success(it) } ?: Result.failure(AppException.NotAuthenticated)
}
