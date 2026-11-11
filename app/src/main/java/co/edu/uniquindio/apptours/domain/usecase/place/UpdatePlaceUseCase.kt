package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.util.Validators
import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import javax.inject.Inject

/** Solo el autor edita; cualquier edición vuelve la publicación a PENDIENTE. */
class UpdatePlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val uploadPlaceImages: UploadPlaceImagesUseCase
) {
    suspend operator fun invoke(
        placeId: String,
        draft: PlaceDraft,
        onUploadProgress: (Float) -> Unit = {}
    ): Result<Unit> {
        draft.validate()?.let { return Result.failure(it) }
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        if (place.authorId != user.id) return Result.failure(AppException.NotAllowed)
        // Las fotos que ya estaban se conservan; solo se suben las nuevas.
        val imageUrls = uploadPlaceImages(draft.photos, onUploadProgress).getOrElse { return Result.failure(it) }

        return placeRepository.update(
            place.copy(
                title = Validators.singleSpaced(draft.title),
                category = requireNotNull(draft.category),
                description = draft.description.trim(),
                priceRange = requireNotNull(draft.priceRange),
                daysActive = draft.daysActive,
                openTime = draft.openTime.trim(),
                closeTime = draft.closeTime.trim(),
                latitude = requireNotNull(draft.latitude),
                longitude = requireNotNull(draft.longitude),
                imageUrls = imageUrls,
                status = PlaceStatus.PENDIENTE,
                rejectionReason = null
            )
        )
    }
}
