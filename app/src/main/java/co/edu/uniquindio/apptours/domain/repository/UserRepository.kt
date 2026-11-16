package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.User
import kotlinx.coroutines.flow.Flow

/** Puntos antes y después de una suma (para saber si el usuario subió de nivel). */
data class PointsChange(val before: Int, val after: Int)

interface UserRepository {
    fun observeUser(id: String): Flow<User?>
    suspend fun getUser(id: String): User?
    suspend fun create(user: User): Result<Unit>
    suspend fun update(user: User): Result<Unit>
    suspend fun delete(id: String): Result<Unit>

    /** Suma puntos y guarda el nivel resultante en la misma transacción. */
    suspend fun addPoints(userId: String, points: Int): Result<PointsChange>

    suspend fun incrementCommentsCount(userId: String): Result<Unit>

    /** Guarda insignias recién ganadas: id → fecha de obtención (millis). */
    suspend fun addBadges(userId: String, badges: Map<String, Long>): Result<Unit>
}
