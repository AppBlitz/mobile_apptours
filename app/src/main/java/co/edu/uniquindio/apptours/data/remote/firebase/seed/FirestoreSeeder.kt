package co.edu.uniquindio.apptours.data.remote.firebase.seed

import android.util.Log
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Carga [SeedData] una sola vez por proyecto de Firebase. La marca `meta/seed` evita
 * repetir la carga; para recargar los datos de prueba basta con borrar ese documento.
 */
@Singleton
class FirestoreSeeder @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun seedIfNeeded() {
        runCatching {
            val marker = db.collection(FirestoreCollections.META).document(FirestoreCollections.SEED_DOC)
            // Se consulta al servidor: sin conexión no se puede saber si ya se cargó.
            if (marker.get(Source.SERVER).await().exists()) return

            val batch = db.batch()
            val users = db.collection(FirestoreCollections.USERS)
            val places = db.collection(FirestoreCollections.PLACES)

            SeedData.users.forEach { user ->
                batch.set(users.document(user.id), user.toDto().toMap())
            }
            SeedData.places.forEach { place ->
                batch.set(places.document(place.id), place.toDto().toMap())
            }
            SeedData.votes.forEach { (placeId, userIds) ->
                userIds.forEach { userId ->
                    val vote = mapOf("userId" to userId, "createdAt" to Timestamp.now())
                    batch.set(places.document(placeId).collection(FirestoreCollections.VOTES).document(userId), vote)
                    batch.set(users.document(userId).collection(FirestoreCollections.VOTED_PLACES).document(placeId), vote)
                }
            }
            SeedData.visits.forEach { (userId, visits) ->
                visits.forEach { visit ->
                    batch.set(
                        users.document(userId).collection(FirestoreCollections.VISITS).document(visit.placeId),
                        visit.toDto().toMap()
                    )
                }
            }
            SeedData.comments.forEach { comment ->
                batch.set(
                    places.document(comment.placeId).collection(FirestoreCollections.COMMENTS).document(comment.id),
                    comment.toDto().toMap()
                )
            }
            SeedData.notifications.forEach { notification ->
                batch.set(
                    db.collection(FirestoreCollections.NOTIFICATIONS).document(notification.id),
                    notification.toDto().toMap()
                )
            }
            batch.set(marker, mapOf("version" to SEED_VERSION, "seededAt" to Timestamp.now()))
            batch.commit().await()
        }.onFailure { error ->
            Log.w(TAG, "No se pudieron cargar los datos semilla", error)
        }
    }

    private companion object {
        const val TAG = "FirestoreSeeder"
        const val SEED_VERSION = 1
    }
}
