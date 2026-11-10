package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.firebase.authCall
import co.edu.uniquindio.apptours.domain.model.AuthAccount
import co.edu.uniquindio.apptours.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/** Sesión con Firebase Auth; queda guardada en el dispositivo entre aperturas de la app. */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun observeSession(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override fun currentUserId(): String? = auth.currentUser?.uid

    override suspend fun login(email: String, password: String): Result<String> = authCall {
        auth.signInWithEmailAndPassword(email, password).await().user.requireUid()
    }

    override suspend fun register(email: String, password: String): Result<String> = authCall {
        auth.createUserWithEmailAndPassword(email, password).await().user.requireUid()
    }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthAccount> = authCall {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw AppException.NotAuthenticated
        AuthAccount(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = user.displayName,
            isNewUser = result.additionalUserInfo?.isNewUser == true
        )
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = authCall {
        // El correo de recuperación llega en el idioma de la app (es / en).
        auth.setLanguageCode(java.util.Locale.getDefault().language)
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    override suspend fun refreshEmail(): String? {
        val user = auth.currentUser ?: return null
        // Sin conexión se queda con el correo que ya conocía.
        runCatching { user.reload().await() }
        return auth.currentUser?.email
    }

    override suspend fun requestEmailChange(newEmail: String): Result<Unit> = authCall {
        val user = auth.currentUser ?: throw AppException.NotAuthenticated
        user.verifyBeforeUpdateEmail(newEmail).await()
        Unit
    }

    override fun canChangeEmail(): Boolean =
        auth.currentUser?.providerData.orEmpty().any { it.providerId == EmailAuthProvider.PROVIDER_ID }

    override fun hasRecentLogin(): Boolean {
        val lastSignIn = auth.currentUser?.metadata?.lastSignInTimestamp ?: return false
        return System.currentTimeMillis() - lastSignIn < RECENT_LOGIN_MILLIS
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> = authCall {
        val user = auth.currentUser ?: throw AppException.NotAuthenticated
        user.delete().await()
        Unit
    }

    private fun FirebaseUser?.requireUid(): String = this?.uid ?: throw AppException.NotAuthenticated

    private companion object {
        /** Firebase pide volver a entrar pasados unos 5 minutos; se deja un margen. */
        const val RECENT_LOGIN_MILLIS = 4 * 60_000L
    }
}
