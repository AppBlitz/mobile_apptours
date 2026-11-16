package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import co.edu.uniquindio.apptours.domain.usecase.auth.GetSessionUseCase
import javax.inject.Inject

/** Solo el autor puede eliminar su publicación. */
class DeletePlaceUseCase @Inject constructor(
    private val getSession: GetSessionUseCase,
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(placeId: String): Result<Unit> {
        val user = getSession.require().getOrElse { return Result.failure(it) }
        val place = placeRepository.getPlace(placeId)
            ?: return Result.failure(AppException.PlaceNotFound)
        if (place.authorId != user.id) return Result.failure(AppException.NotAllowed)
        return placeRepository.delete(placeId)
    }
}
