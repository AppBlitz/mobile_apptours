package co.edu.uniquindio.apptours.core.auth

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import co.edu.uniquindio.apptours.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

/** Resultado de pedirle al sistema una cuenta de Google. */
sealed interface GoogleTokenResult {
    data class Success(val idToken: String) : GoogleTokenResult
    /** El usuario cerró el selector: no se muestra ningún error. */
    data object Cancelled : GoogleTokenResult
    data class Failure(@StringRes val messageRes: Int) : GoogleTokenResult
}

/**
 * Abre el selector de cuentas de Google del sistema y devuelve el token de identidad.
 * Necesita el contexto de la actividad porque muestra una ventana del sistema.
 */
suspend fun requestGoogleIdToken(activityContext: Context): GoogleTokenResult {
    val option = GetSignInWithGoogleOption.Builder(
        activityContext.getString(R.string.default_web_client_id)
    ).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
    return try {
        val credential = CredentialManager.create(activityContext)
            .getCredential(activityContext, request)
            .credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            GoogleTokenResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleTokenResult.Failure(R.string.error_google_sign_in)
        }
    } catch (_: GetCredentialCancellationException) {
        GoogleTokenResult.Cancelled
    } catch (_: NoCredentialException) {
        GoogleTokenResult.Failure(R.string.error_google_no_account)
    } catch (_: GetCredentialException) {
        GoogleTokenResult.Failure(R.string.error_google_sign_in)
    } catch (_: GoogleIdTokenParsingException) {
        GoogleTokenResult.Failure(R.string.error_google_sign_in)
    }
}

/** Devuelve una función que abre el selector de Google y entrega el resultado a [onResult]. */
@Composable
fun rememberGoogleSignInLauncher(onResult: (GoogleTokenResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnResult = rememberUpdatedState(onResult)
    return remember(context, scope) {
        { scope.launch { currentOnResult.value(requestGoogleIdToken(context)) } }
    }
}
