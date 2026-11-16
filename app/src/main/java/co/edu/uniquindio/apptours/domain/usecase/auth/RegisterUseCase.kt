package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.validation.AuthForms
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
        val errors = AuthForms.register(fullName, email, password, passwordConfirmation, termsAccepted)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))

        val cleanEmail = email.trim().lowercase()
        return authRepository.register(cleanEmail, password).mapCatching { uid ->
            val user = User(
                id = uid,
                fullName = Validators.singleSpaced(fullName),
                email = cleanEmail,
                role = UserRole.USER,
                latitude = latitude,
                longitude = longitude
            )
            userRepository.create(user).onFailure { error ->
                // Sin perfil la cuenta no sirve: se borra para que pueda registrarse de nuevo.
                authRepository.deleteAccount()
                throw error
            }
            user
        }
    }
}
