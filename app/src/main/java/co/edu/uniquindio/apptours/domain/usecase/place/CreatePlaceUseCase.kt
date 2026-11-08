package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import javax.inject.Inject

/** Crea una publicación en estado PENDIENTE (U-08) y otorga los puntos de publicar. */
class CreatePlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val awardPoints: AwardPointsUseCase
) {
    suspend operator fun invoke(draft: PlaceDraft): Result<String> {
        draft.validate()?.let { return Result.failure(it) }
        val author = getSession.require().getOrElse { return Result.failure(it) }

        val place = Place(
            id = "",
            title = draft.title.trim(),
            category = draft.category,
            description = draft.description.trim(),
            priceRange = draft.priceRange,
            daysActive = draft.daysActive.ifEmpty { Place.DEFAULT_DAYS },
            openTime = draft.openTime,
            closeTime = draft.closeTime,
            latitude = draft.latitude,
            longitude = draft.longitude,
            photosCount = draft.photosCount.coerceIn(1, MAX_PHOTOS),
            authorId = author.id,
            authorName = author.fullName,
            authorLevel = author.level,
            status = PlaceStatus.PENDIENTE
        )
        return placeRepository.create(place).onSuccess {
            awardPoints(author.id, PointAction.CREATE_PLACE)
        }
    }
}
