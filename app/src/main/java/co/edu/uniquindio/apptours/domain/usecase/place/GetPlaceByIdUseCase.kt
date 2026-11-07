package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlaceByIdUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    operator fun invoke(placeId: String): Flow<Place?> = placeRepository.observePlace(placeId)
}
