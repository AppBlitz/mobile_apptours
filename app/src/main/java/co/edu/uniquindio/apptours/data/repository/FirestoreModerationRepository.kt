package co.edu.uniquindio.apptours.data.repository

import co.edu.uniquindio.apptours.core.common.AppException
import co.edu.uniquindio.apptours.data.remote.firebase.FirestoreCollections
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toEnumOr
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toMillis
import co.edu.uniquindio.apptours.data.remote.firebase.dto.toTimestamp
import co.edu.uniquindio.apptours.data.remote.firebase.firestoreCall
import co.edu.uniquindio.apptours.data.remote.firebase.observe
import co.edu.uniquindio.apptours.domain.model.ModerationAction
import co.edu.uniquindio.apptours.domain.model.ModerationDecision
import co.edu.uniquindio.apptours.domain.model.ModerationLogEntry
import co.edu.uniquindio.apptours.domain.model.ModeratorStats
import co.edu.uniquindio.apptours.domain.repository.ModerationRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * `moderationLog/{id}` (historial) y `moderatorStats/{uid}` (contadores, XP y trofeos).
 * Las reglas solo aceptan un cambio en moderatorStats si en el mismo lote se crea su entrada del historial.
 */
@Singleton
class FirestoreModerationRepository @Inject constructor(
    private val db: FirebaseFirestore
) : ModerationRepository {

    private val log = db.collection(FirestoreCollections.MODERATION_LOG)
    private val stats = db.collection(FirestoreCollections.MODERATOR_STATS)

    override fun observeStats(moderatorId: String): Flow<ModeratorStats> =
        stats.document(moderatorId).observe { it.toStats() }.map { it ?: ModeratorStats() }

    // Sin orderBy para no exigir un índice compuesto; el caso de uso ordena.
    override fun observeLog(moderatorId: String): Flow<List<ModerationLogEntry>> =
        log.whereEqualTo("moderatorId", moderatorId).observe { it.toLogEntry() }

    override suspend fun applyDecision(decision: ModerationDecision): Result<Unit> = firestoreCall {
        val placeRef = db.collection(FirestoreCollections.PLACES).document(decision.placeId)
        if (!placeRef.get().await().exists()) throw AppException.PlaceNotFound
        val logRef = log.document()
        val now = Timestamp.now()
        val batch = db.batch()

        batch.update(
            placeRef,
            mapOf(
                "status" to decision.action.resultingStatus.name,
                "rejectionReason" to decision.reason.takeIf { decision.action == ModerationAction.REJECTED },
                "moderatorId" to decision.moderatorId,
                "reviewedAt" to now
            )
        )

        batch.set(
            logRef,
            mapOf(
                "moderatorId" to decision.moderatorId,
                "placeId" to decision.placeId,
                "placeTitle" to decision.placeTitle,
                "action" to decision.action.name,
                "reason" to decision.reason,
                "xpAwarded" to decision.xpAwarded,
                "createdAt" to now
            )
        )

        val counter = when (decision.action) {
            ModerationAction.VERIFIED -> "verifiedCount"
            ModerationAction.REJECTED -> "rejectedCount"
            ModerationAction.RESOLVED -> "resolvedCount"
        }
        val statsUpdate = buildMap<String, Any> {
            put("xp", FieldValue.increment(decision.xpAwarded.toLong()))
            put("reviewedCount", FieldValue.increment(1))
            put(counter, FieldValue.increment(1))
            put("lastLogId", logRef.id)
            if (decision.newTrophies.isNotEmpty()) {
                put("trophies", decision.newTrophies.mapValues { it.value.toTimestamp() })
            }
        }
        // merge: crea el documento la primera vez y, con "trophies", agrega claves sin borrar las anteriores.
        batch.set(stats.document(decision.moderatorId), statsUpdate, SetOptions.merge())

        batch.commit().await()
        Unit
    }

    private fun DocumentSnapshot.toStats(): ModeratorStats = ModeratorStats(
        xp = getLong("xp")?.toInt() ?: 0,
        reviewedCount = getLong("reviewedCount")?.toInt() ?: 0,
        verifiedCount = getLong("verifiedCount")?.toInt() ?: 0,
        rejectedCount = getLong("rejectedCount")?.toInt() ?: 0,
        resolvedCount = getLong("resolvedCount")?.toInt() ?: 0,
        trophies = (get("trophies") as? Map<*, *>).orEmpty()
            .mapNotNull { (key, value) ->
                val id = key as? String ?: return@mapNotNull null
                val date = value as? Timestamp ?: return@mapNotNull null
                id to date.toMillis()
            }
            .toMap()
    )

    private fun DocumentSnapshot.toLogEntry(): ModerationLogEntry = ModerationLogEntry(
        id = id,
        moderatorId = getString("moderatorId").orEmpty(),
        placeId = getString("placeId").orEmpty(),
        placeTitle = getString("placeTitle").orEmpty(),
        action = getString("action").orEmpty().toEnumOr(ModerationAction.VERIFIED),
        reason = getString("reason"),
        xpAwarded = getLong("xpAwarded")?.toInt() ?: 0,
        createdAt = getTimestamp("createdAt")?.toMillis() ?: System.currentTimeMillis()
    )
}
