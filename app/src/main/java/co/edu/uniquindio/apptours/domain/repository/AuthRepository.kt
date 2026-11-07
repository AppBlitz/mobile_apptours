package co.edu.uniquindio.apptours.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Emite el id del usuario autenticado o null si no hay sesión. */
    fun observeSession(): Flow<String?>
    fun currentUserId(): String?
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
}
