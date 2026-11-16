package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Entrar o registrarse con Google: si la cuenta ya tiene perfil se usa ese (y su rol);
 * si es la primera vez se crea el perfil como usuario con el nombre de la cuenta de Google.
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val getSession: GetSessionUseCase
) {
    suspend operator fun invoke(idToken: String): Result<User> =
        authRepository.signInWithGoogle(idToken).mapCatching { account ->
            getSession.restore() ?: run {
                val email = account.email.lowercase()
                val user = User(
                    id = account.uid,
                    fullName = account.displayName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@"),
                    email = email,
                    role = UserRole.USER
                )
                userRepository.create(user).onFailure { error ->
                    authRepository.logout()
                    throw error
                }
                user
            }
        }
}
