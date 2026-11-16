package co.edu.uniquindio.apptours.domain.usecase.auth

import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}
