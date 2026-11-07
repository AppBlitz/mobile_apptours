package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import javax.inject.Inject

/** Único punto donde se otorgan puntos, según la tabla de [PointAction]. */
class AwardPointsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, action: PointAction): Result<Unit> =
        userRepository.addPoints(userId, action.points)
}
