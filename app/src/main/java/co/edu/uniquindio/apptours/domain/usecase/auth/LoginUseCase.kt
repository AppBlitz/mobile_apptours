package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val invalidFields = buildSet {
            if (!Validators.isValidEmail(email)) add(FormField.EMAIL)
            if (password.isBlank()) add(FormField.PASSWORD)
        }
        if (invalidFields.isNotEmpty()) {
            return Result.failure(AppException.InvalidFields(invalidFields))
        }
        return authRepository.login(email.trim().lowercase(), password).mapCatching { uid ->
            userRepository.getUser(uid) ?: throw AppException.UserNotFound
        }
    }
}
