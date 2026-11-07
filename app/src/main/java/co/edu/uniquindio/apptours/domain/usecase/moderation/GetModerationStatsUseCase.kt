package co.edu.uniquindio.apptours.domain.usecase.moderation

import co.edu.uniquindio.apptours.domain.model.ModerationStats
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

/** Contadores del panel: pendientes y verificadas/rechazadas hoy (según `reviewedAt`). */
class GetModerationStatsUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(): Flow<ModerationStats> =
        placeRepository.observeAll().map { places ->
            val startOfToday = startOfToday()
            val reviewedToday = places.filter { (it.reviewedAt ?: 0L) >= startOfToday }
            ModerationStats(
                pendingCount = places.count { it.status == PlaceStatus.PENDIENTE },
                verifiedToday = reviewedToday.count { it.status == PlaceStatus.VERIFICADA },
                rejectedToday = reviewedToday.count { it.status == PlaceStatus.RECHAZADA }
            )
        }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
