package co.edu.uniquindio.apptours.domain.usecase.user

import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.validation.AuthForms
import javax.inject.Inject

/**
 * Guarda nombre y ubicación. Si el correo cambió, se envía un enlace de confirmación al nuevo:
 * el perfil conserva el correo anterior hasta que el usuario lo confirme.
 *
 * Devuelve el correo al que se envió el enlace, o null si el correo no cambió.
 */
class UpdateProfileUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    /** Las cuentas de Google no cambian el correo desde aquí. */
    fun canChangeEmail(): Boolean = authRepository.canChangeEmail()

    suspend operator fun invoke(
        fullName: String,
        email: String,
        latitude: Double,
        longitude: Double
    ): Result<String?> {
        val errors = AuthForms.profile(fullName, email)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))

        val user = getSession.require().getOrElse { return Result.failure(it) }
        val newEmail = email.trim().lowercase()
        val emailChanged = newEmail != user.email.lowercase()
        if (emailChanged) {
            if (!authRepository.canChangeEmail()) return Result.failure(AppException.NotAllowed)
            authRepository.requestEmailChange(newEmail).getOrElse { return Result.failure(it) }
        }

        return userRepository.update(
            user.copy(fullName = Validators.singleSpaced(fullName), latitude = latitude, longitude = longitude)
        ).map { if (emailChanged) newEmail else null }
    }
}
