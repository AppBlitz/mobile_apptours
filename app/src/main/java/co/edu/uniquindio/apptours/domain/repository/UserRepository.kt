package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(id: String): Flow<User?>
    suspend fun getUser(id: String): User?
    suspend fun create(user: User): Result<Unit>
    suspend fun update(user: User): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
    suspend fun addPoints(userId: String, points: Int): Result<Unit>
}
