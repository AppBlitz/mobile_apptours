package co.edu.uniquindio.apptours.domain.repository

import co.edu.uniquindio.apptours.domain.model.AuthAccount
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Emite el id del usuario autenticado o null si no hay sesión. */
    fun observeSession(): Flow<String?>
    fun currentUserId(): String?
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String): Result<String>
    /** Inicia sesión (o crea la cuenta si es la primera vez) con el token de una cuenta de Google. */
    suspend fun signInWithGoogle(idToken: String): Result<AuthAccount>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    /** Recarga la cuenta y devuelve su correo actual (cambia al confirmar un cambio de correo). */
    suspend fun refreshEmail(): String?
    /** Envía un enlace al correo nuevo; el cambio se aplica cuando el usuario lo confirma. */
    suspend fun requestEmailChange(newEmail: String): Result<Unit>
    /** Solo las cuentas con contraseña cambian el correo; en las de Google lo administra Google. */
    fun canChangeEmail(): Boolean
    /** Las operaciones sensibles (eliminar cuenta, cambiar correo) exigen haber entrado hace poco. */
    fun hasRecentLogin(): Boolean
    suspend fun logout()
    suspend fun deleteAccount(): Result<Unit>
}
