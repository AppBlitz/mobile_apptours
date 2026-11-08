package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.model.PlaceDraft
import co.edu.uniquindio.apptours.domain.model.PlaceStatus
import co.edu.uniquindio.apptours.domain.model.PointAction
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.AwardPointsUseCase
import co.edu.uniquindio.apptours.domain.usecase.gamification.EvaluateBadgesUseCase
import co.edu.uniquindio.apptours.domain.validation.PlaceForms
import javax.inject.Inject

/** Crea una publicación en estado PENDIENTE (U-08) y otorga los puntos de publicar. */
class CreatePlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository,
    private val awardPoints: AwardPointsUseCase,
    private val evaluateBadges: EvaluateBadgesUseCase
) {
    suspend operator fun invoke(draft: PlaceDraft): Result<String> {
        draft.validate()?.let { return Result.failure(it) }
        val author = getSession.require().getOrElse { return Result.failure(it) }

        // Tras validar, categoría, precio y ubicación ya no son nulos.
        val place = Place(
            id = "",
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
            authorId = author.id,
            authorName = author.fullName,
            authorLevel = author.level,
            status = PlaceStatus.PENDIENTE
        )
        return placeRepository.create(place).onSuccess {
            awardPoints(author.id, PointAction.CREATE_PLACE)
            evaluateBadges()
        }
    }
}
