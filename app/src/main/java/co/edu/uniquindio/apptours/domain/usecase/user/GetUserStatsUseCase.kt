package co.edu.uniquindio.apptours.domain.usecase.user

import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.UserStats
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Dashboard del perfil (U-17) con conteos reales del usuario autenticado. */
class GetUserStatsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository,
    private val visitRepository: VisitRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<UserStats> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) return@flatMapLatest flowOf(UserStats())
            combine(
                placeRepository.observePlacesByAuthor(uid),
                visitRepository.observeVisits(uid)
            ) { places, visits ->
                UserStats(
                    activeCount = places.count { it.status == PlaceStatus.VERIFICADA },
                    finishedCount = places.count { it.status == PlaceStatus.RESUELTA },
                    pendingCount = places.count { it.status == PlaceStatus.PENDIENTE },
                    rejectedCount = places.count { it.status == PlaceStatus.RECHAZADA },
                    verifiedCount = places.count { it.status.isPublic },
                    visitedCount = visits.size,
                    votesReceived = places.sumOf { it.votesCount }
                )
            }
        }
}
