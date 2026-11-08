package co.edu.uniquindio.apptours.data.remote.firebase

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.core.common.FieldError
import co.edu.uniquindio.apptours.core.common.FormField
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/** Ejecuta una operación de Firebase Auth y traduce sus errores a [AppException]. */
internal suspend fun <T> authCall(block: suspend () -> T): Result<T> =
    runCatching { block() }.recoverCatching { throw it.toAuthAppException() }

internal fun Throwable.toAuthAppException(): Throwable = when (this) {
    is AppException -> this
    // Va antes que InvalidCredentials porque la extiende.
    is FirebaseAuthWeakPasswordException -> AppException.WeakPassword
    is FirebaseAuthInvalidCredentialsException ->
        if (errorCode == "ERROR_INVALID_EMAIL") {
            AppException.InvalidFields(mapOf(FormField.EMAIL to FieldError.InvalidEmail))
        } else {
            AppException.InvalidCredentials
        }
    is FirebaseAuthInvalidUserException ->
        if (errorCode == "ERROR_USER_DISABLED") AppException.AccountDisabled else AppException.InvalidCredentials
    is FirebaseAuthUserCollisionException -> AppException.EmailAlreadyInUse
    is FirebaseAuthRecentLoginRequiredException -> AppException.RecentLoginRequired
    is FirebaseTooManyRequestsException -> AppException.TooManyAttempts
    is FirebaseNetworkException -> AppException.Network
    else -> toAppException()
}
