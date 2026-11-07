package co.edu.uniquindio.apptours.domain.usecase.place

import co.edu.uniquindio.apptours.domain.model.Place
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.PlaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** Publicaciones del usuario autenticado en cualquier estado (pantalla "Mis publicaciones", IT-4). */
class GetMyPlacesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val placeRepository: PlaceRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Place>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else placeRepository.observePlacesByAuthor(uid)
        }
}
