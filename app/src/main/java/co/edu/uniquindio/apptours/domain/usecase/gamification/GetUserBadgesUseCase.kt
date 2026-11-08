package co.edu.uniquindio.apptours.domain.usecase.gamification

import co.edu.uniquindio.apptours.domain.model.Badge
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Insignias del usuario en sesión con progreso real y fecha de obtención, en tiempo real. */
class GetUserBadgesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Badge>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(emptyList())
            combine(
                userRepository.observeUser(uid),
                placeRepository.observePlacesByAuthor(uid),
                visitRepository.observeVisits(uid),
                placeRepository.observePublicPlaces()
            ) { user, own, visits, public ->
                if (user == null) emptyList()
                else BadgeCalculator.calculate(user, own, visits, public)
            }
        }
}
