package co.edu.uniquindio.apptours.domain.usecase.visit

import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetMyVisitsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val visitRepository: VisitRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Visit>> =
        authRepository.observeSession().flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList()) else visitRepository.observeVisits(uid)
        }
}
