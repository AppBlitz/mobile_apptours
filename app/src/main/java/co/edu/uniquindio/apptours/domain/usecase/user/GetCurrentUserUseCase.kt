package co.edu.uniquindio.apptours.domain.usecase.user

import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Usuario autenticado en tiempo real (se actualiza al ganar puntos, editar el perfil, etc.). */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<User?> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) flowOf(null) else userRepository.observeUser(uid)
        }
}
