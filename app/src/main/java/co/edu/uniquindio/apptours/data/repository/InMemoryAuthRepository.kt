package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource
import co.edu.uniquindio.apptours.data.local.InMemoryDataSource.Credential
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** TODO(IT-3): reemplazar por FirebaseAuthRepository. */
@Singleton
class InMemoryAuthRepository @Inject constructor() : AuthRepository {

    private val credentials = MutableStateFlow(InMemoryDataSource.credentials)
    private val session = MutableStateFlow<String?>(null)

    override fun observeSession(): Flow<String?> = session.asStateFlow()

    override fun currentUserId(): String? = session.value

    override suspend fun login(email: String, password: String): Result<String> {
        val found = credentials.value.find { it.email == email && it.password == password }
            ?: return Result.failure(AppException.InvalidCredentials)
        session.value = found.uid
        return Result.success(found.uid)
    }

    override suspend fun register(email: String, password: String): Result<String> {
        if (credentials.value.any { it.email == email }) {
            return Result.failure(AppException.EmailAlreadyInUse)
        }
        val uid = "user_${UUID.randomUUID().toString().take(8)}"
        credentials.update { it + Credential(uid, email, password) }
        session.value = uid
        return Result.success(uid)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = Result.success(Unit)

    override suspend fun logout() {
        session.value = null
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val uid = session.value ?: return Result.failure(AppException.NotAuthenticated)
        credentials.update { list -> list.filterNot { it.uid == uid } }
        session.value = null
        return Result.success(Unit)
    }
}
