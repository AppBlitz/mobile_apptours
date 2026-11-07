package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.Visit
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    fun observeVisits(userId: String): Flow<List<Visit>>

    /** Marca o desmarca el lugar como visitado; devuelve true si quedó visitado. */
    suspend fun toggleVisit(userId: String, visit: Visit): Result<Boolean>
    suspend fun deleteAllForUser(userId: String): Result<Unit>
}
