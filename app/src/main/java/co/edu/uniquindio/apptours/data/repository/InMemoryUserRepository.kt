package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.domain.model.User
import co.edu.uniquindio.apptours.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-2): reemplazar por FirestoreUserRepository. */
@Singleton
class InMemoryUserRepository @Inject constructor() : UserRepository {

    private val users = MutableStateFlow(InMemoryDataSource.users.associateBy { it.id })

    override fun observeUser(id: String): Flow<User?> = users.map { it[id] }

    override suspend fun getUser(id: String): User? = users.value[id]

    override suspend fun create(user: User): Result<Unit> {
        users.update { it + (user.id to user) }
        return Result.success(Unit)
    }

    override suspend fun update(user: User): Result<Unit> {
        if (user.id !in users.value) return Result.failure(AppException.UserNotFound)
        users.update { it + (user.id to user) }
        return Result.success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        users.update { it - id }
        return Result.success(Unit)
    }

    override suspend fun addPoints(userId: String, points: Int): Result<Unit> {
        val user = users.value[userId] ?: return Result.failure(AppException.UserNotFound)
        users.update { it + (userId to user.copy(points = user.points + points)) }
        return Result.success(Unit)
    }
}
