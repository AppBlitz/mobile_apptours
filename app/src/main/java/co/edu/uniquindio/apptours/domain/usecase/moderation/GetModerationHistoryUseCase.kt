package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Historial del moderador en sesión: lo que verificó, rechazó o resolvió, lo más reciente primero.
 * Se excluyen las que el autor editó después (vuelven a PENDIENTE y esperan una nueva revisión).
 */
class GetModerationHistoryUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Place>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                placeRepository.observeReviewedBy(uid).map { list ->
                    list.filter { it.reviewedAt != null && it.status != PlaceStatus.PENDIENTE }
                        .sortedByDescending { it.reviewedAt }
                }
            }
        }
}
