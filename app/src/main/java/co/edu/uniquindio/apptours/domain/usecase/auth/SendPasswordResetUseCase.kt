package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import javax.inject.Inject

/** Recuperación de contraseña; aplica igual para usuarios y moderadores. */
class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (!Validators.isValidEmail(email)) {
            return Result.failure(AppException.InvalidFields(setOf(FormField.EMAIL)))
        }
        return authRepository.sendPasswordReset(email.trim().lowercase())
    }
}
