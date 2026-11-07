package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import javax.inject.Inject

/** Elimina la cuenta y todo lo que pertenece al usuario (publicaciones, visitas, perfil). */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val uid = authRepository.currentUserId() ?: throw AppException.NotAuthenticated
        placeRepository.deleteByAuthor(uid).getOrThrow()
        visitRepository.deleteAllForUser(uid).getOrThrow()
        userRepository.delete(uid).getOrThrow()
        authRepository.deleteAccount().getOrThrow()
    }
}
