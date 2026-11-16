package co.edu.uniquindio.apptours.data.remote.firebase.seed

import android.content.Context
import android.util.Log
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Carga [SeedData] una sola vez por proyecto de Firebase. La marca `meta/seed` evita
 * repetir la carga; para recargar los datos de prueba basta con borrar ese documento.
 *
 * Primero crea las cuentas de prueba en Firebase Auth (usuarios y moderadores precargados)
 * y luego escribe los datos con sus uid reales. `firestore.rules` solo permite esta carga
 * mientras `meta/seed` no existe.
 */
@Singleton
class FirestoreSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: FirebaseFirestore
) {

    suspend fun seedIfNeeded() {
        runCatching {
            val marker = db.collection(FirestoreCollections.META).document(FirestoreCollections.SEED_DOC)
            // Se consulta al servidor: sin conexión no se puede saber si ya se cargó.
            if (marker.get(Source.SERVER).await().exists()) return

            val uids = createSeedAccounts()
            fun uid(seedId: String) = uids[seedId] ?: seedId

            val users = db.collection(FirestoreCollections.USERS)
            val places = db.collection(FirestoreCollections.PLACES)
            val writes = mutableListOf<Pair<DocumentReference, Map<String, Any?>>>()

            SeedData.users.forEach { user ->
                writes += users.document(uid(user.id)) to user.copy(id = uid(user.id)).toDto().toMap()
            }
            SeedData.places.forEach { place ->
                val seeded = place.copy(
                    authorId = uid(place.authorId),
                    moderatorId = place.moderatorId?.let(::uid)
                )
                writes += places.document(place.id) to seeded.toDto().toMap()
            }
            SeedData.votes.forEach { (placeId, userIds) ->
                userIds.map(::uid).forEach { userId ->
                    val vote = mapOf("userId" to userId, "createdAt" to Timestamp.now())
                    writes += places.document(placeId).collection(FirestoreCollections.VOTES).document(userId) to vote
                    writes += users.document(userId).collection(FirestoreCollections.VOTED_PLACES).document(placeId) to vote
                }
            }
            SeedData.visits.forEach { (userId, visits) ->
                visits.forEach { visit ->
                    writes += users.document(uid(userId)).collection(FirestoreCollections.VISITS)
                        .document(visit.placeId) to visit.toDto().toMap()
                }
            }
            SeedData.comments.forEach { comment ->
                writes += places.document(comment.placeId).collection(FirestoreCollections.COMMENTS)
                    .document(comment.id) to comment.copy(authorId = uid(comment.authorId)).toDto().toMap()
            }
            SeedData.notifications.forEach { notification ->
                writes += db.collection(FirestoreCollections.NOTIFICATIONS).document(notification.id) to
                    notification.copy(userId = uid(notification.userId)).toDto().toMap()
            }

            // Lotes pequeños: cada escritura evalúa la regla de carga inicial y las reglas
            // limitan las consultas por lote. Si algo falla, la marca no se escribe y se
            // reintenta en la próxima apertura (los set sobrescriben lo ya cargado).
            writes.chunked(BATCH_SIZE).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { (ref, data) -> batch.set(ref, data) }
                batch.commit().await()
            }
            marker.set(mapOf("version" to SEED_VERSION, "seededAt" to Timestamp.now())).await()
        }.onFailure { error ->
            Log.w(TAG, "No se pudieron cargar los datos semilla", error)
        }
    }

    /**
     * Crea (o reutiliza si ya existen) las cuentas de [SeedData.credentials] y devuelve
     * id semilla -> uid real. Usa una instancia aparte de Firebase para no tocar la sesión
     * de quien tiene la app abierta.
     */
    private suspend fun createSeedAccounts(): Map<String, String> {
        val app = FirebaseApp.getApps(context).firstOrNull { it.name == SEED_APP }
            ?: FirebaseApp.initializeApp(context, FirebaseApp.getInstance().options, SEED_APP)
        val auth = FirebaseAuth.getInstance(app)
        return try {
            SeedData.credentials.associate { credential ->
                val user = try {
                    auth.createUserWithEmailAndPassword(credential.email, credential.password).await().user
                } catch (_: FirebaseAuthUserCollisionException) {
                    auth.signInWithEmailAndPassword(credential.email, credential.password).await().user
                }
                credential.uid to requireNotNull(user).uid
            }
        } finally {
            auth.signOut()
        }
    }

    private companion object {
        const val TAG = "FirestoreSeeder"
        const val SEED_APP = "seeder"
        const val SEED_VERSION = 2
        const val BATCH_SIZE = 8
    }
}
