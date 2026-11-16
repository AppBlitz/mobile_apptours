package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Ids de los lugares en los que el usuario autenticado marcó "Me interesa". */
class GetMyVotesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Set<String>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) flowOf(emptySet()) else placeRepository.observeVotedPlaceIds(uid)
        }
}
