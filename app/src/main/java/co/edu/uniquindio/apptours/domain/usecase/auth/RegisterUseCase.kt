package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FormField
import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import javax.inject.Inject

/** Registro de usuarios (los moderadores no se registran: vienen precargados). */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        termsAccepted: Boolean,
        latitude: Double,
        longitude: Double
    ): Result<User> {
        val invalidFields = buildSet {
            if (fullName.isBlank()) add(FormField.FULL_NAME)
            if (!Validators.isValidEmail(email)) add(FormField.EMAIL)
            if (!Validators.isValidPassword(password)) add(FormField.PASSWORD)
            if (password != passwordConfirmation) add(FormField.PASSWORD_CONFIRMATION)
            if (!termsAccepted) add(FormField.TERMS)
        }
        if (invalidFields.isNotEmpty()) {
            return Result.failure(AppException.InvalidFields(invalidFields))
        }

        val cleanEmail = email.trim().lowercase()
        return authRepository.register(cleanEmail, password).mapCatching { uid ->
            val user = User(
                id = uid,
                fullName = fullName.trim(),
                email = cleanEmail,
                role = UserRole.USER,
                latitude = latitude,
                longitude = longitude
            )
            userRepository.create(user).getOrThrow()
            user
        }
    }
}
