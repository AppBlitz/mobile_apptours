package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationFilter
import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Cola de moderación por estado, las más antiguas primero. */
class GetModerationQueueUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(filter: ModerationFilter): Flow<List<Place>> =
        placeRepository.observeByStatus(filter.statuses).map { list ->
            list.sortedBy { it.createdAt }
        }
}
