package co.edu.uniquindio.apptours.data.remote.firebase

import co.edu.uniquindio.apptours.core.common.AppException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** Escucha la consulta en tiempo real y emite los documentos ya convertidos. */
internal fun <T> Query.observe(mapper: (DocumentSnapshot) -> T?): Flow<List<T>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error.toAppException())
            return@addSnapshotListener
        }
        if (snapshot != null) trySend(snapshot.documents.mapNotNull(mapper))
    }
    awaitClose { registration.remove() }
}

/** Escucha un documento en tiempo real; emite null si no existe. */
internal fun <T> DocumentReference.observe(mapper: (DocumentSnapshot) -> T?): Flow<T?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error.toAppException())
            return@addSnapshotListener
        }
        trySend(snapshot?.takeIf { it.exists() }?.let(mapper))
    }
    awaitClose { registration.remove() }
}

/** Ejecuta una operación de Firestore y traduce sus errores a [AppException]. */
internal suspend fun <T> firestoreCall(block: suspend () -> T): Result<T> =
    runCatching { block() }.recoverCatching { throw it.toAppException() }

internal fun Throwable.toAppException(): Throwable = when (this) {
    is AppException -> this
    is FirebaseFirestoreException -> when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED,
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> AppException.NotAllowed
        FirebaseFirestoreException.Code.UNAVAILABLE,
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> AppException.Network
        else -> this
    }
    else -> this
}
