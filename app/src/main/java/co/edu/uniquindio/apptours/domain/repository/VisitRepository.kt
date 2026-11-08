package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.Visit
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    fun observeVisits(userId: String): Flow<List<Visit>>

    /** Marca o desmarca el lugar como visitado y mueve `visitsCount`; devuelve true si quedó visitado. */
    suspend fun toggleVisit(userId: String, visit: Visit): Result<Boolean>

    /** Guarda la experiencia (texto opcional + calificación) en la visita ya registrada. */
    suspend fun saveExperience(userId: String, placeId: String, experience: String?, rating: Int): Result<Unit>

    suspend fun deleteAllForUser(userId: String): Result<Unit>
}
