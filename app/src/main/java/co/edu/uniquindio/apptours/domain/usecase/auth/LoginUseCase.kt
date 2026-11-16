package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.validation.AuthForms
import javax.inject.Inject

/** Inicio de sesión con correo; el rol del documento `users` decide a qué grafo se navega. */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val getSession: GetSessionUseCase
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val errors = AuthForms.login(email, password)
        if (errors.isNotEmpty()) return Result.failure(AppException.InvalidFields(errors))

        return authRepository.login(email.trim().lowercase(), password).mapCatching {
            getSession.restore() ?: run {
                // La cuenta existe en Auth pero no tiene perfil: no se deja la sesión a medias.
                authRepository.logout()
                throw AppException.UserNotFound
            }
        }
    }
}
