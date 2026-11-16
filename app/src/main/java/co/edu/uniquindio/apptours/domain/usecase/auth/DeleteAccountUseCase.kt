package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import javax.inject.Inject

/** Elimina la cuenta y todo lo que pertenece al usuario (publicaciones, votos, visitas, perfil). */
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val uid = authRepository.currentUserId() ?: throw AppException.NotAuthenticated
        // Se comprueba antes de borrar datos: si Firebase rechazara el borrado de la cuenta al
        // final, quedaría una cuenta sin perfil.
        if (!authRepository.hasRecentLogin()) throw AppException.RecentLoginRequired
        placeRepository.deleteByAuthor(uid).getOrThrow()
        visitRepository.deleteAllForUser(uid).getOrThrow()
        userRepository.delete(uid).getOrThrow()
        authRepository.deleteAccount().getOrThrow()
    }
}
