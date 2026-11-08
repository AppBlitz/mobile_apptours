package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.validation.AuthForms
import javax.inject.Inject

/** Recuperación de contraseña; aplica igual para usuarios y moderadores. */
class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val errors = AuthForms.passwordReset(email)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))
        return authRepository.sendPasswordReset(email.trim().lowercase())
    }
}
