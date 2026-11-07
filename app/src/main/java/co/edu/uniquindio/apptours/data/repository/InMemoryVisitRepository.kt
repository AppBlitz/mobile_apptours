package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.Visit
import co.edu.uniquindio.apptours.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-2): reemplazar por FirestoreVisitRepository. */
@Singleton
class InMemoryVisitRepository @Inject constructor() : VisitRepository {

    /** userId -> visitas. */
    private val visits = MutableStateFlow(InMemoryDataSource.visits)

    override fun observeVisits(userId: String): Flow<List<Visit>> =
        visits.map { it[userId].orEmpty() }

    override suspend fun toggleVisit(userId: String, visit: Visit): Result<Boolean> {
        val current = visits.value[userId].orEmpty()
        val alreadyVisited = current.any { it.placeId == visit.placeId }
        val updated = if (alreadyVisited) {
            current.filterNot { it.placeId == visit.placeId }
        } else {
            current + visit
        }
        visits.update { it + (userId to updated) }
        return Result.success(!alreadyVisited)
    }

    override suspend fun deleteAllForUser(userId: String): Result<Unit> {
        visits.update { it - userId }
        return Result.success(Unit)
    }
}
