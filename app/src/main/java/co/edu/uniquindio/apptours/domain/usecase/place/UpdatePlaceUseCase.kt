package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import javax.inject.Inject

/** Solo el autor edita; cualquier edición vuelve la publicación a PENDIENTE. */
class UpdatePlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(placeId: String, draft: PlaceDraft): Result<Unit> {
        draft.validate()?.let { return Result.failure(it) }
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        if (place.authorId != user.id) return Result.failure(AppException.NotAllowed)

        return placeRepository.update(
            place.copy(
                title = draft.title.trim(),
                category = requireNotNull(draft.category),
                description = draft.description.trim(),
                priceRange = requireNotNull(draft.priceRange),
                daysActive = draft.daysActive,
                openTime = draft.openTime.trim(),
                closeTime = draft.closeTime.trim(),
                latitude = requireNotNull(draft.latitude),
                longitude = requireNotNull(draft.longitude),
                photosCount = draft.photosCount.coerceIn(1, PlaceForms.MAX_PHOTOS),
                status = PlaceStatus.PENDIENTE,
                rejectionReason = null
            )
        )
    }
}
