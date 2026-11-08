package co.edu.uniquindio.apptours.data.remote.firebase

/**
 * Estructura de la base de datos:
 *
 * users/{uid}
 * users/{uid}/visits/{placeId}
 * users/{uid}/votedPlaces/{placeId}   espejo de los votos del usuario
 * places/{placeId}
 * places/{placeId}/votes/{uid}
 * places/{placeId}/comments/{commentId}
 * notifications/{notificationId}      con el campo userId del destinatario
 * meta/seed                           marca de que los datos semilla ya se cargaron
 */
object FirestoreCollections {
    const val USERS = "users"
    const val VISITS = "visits"
    const val VOTED_PLACES = "votedPlaces"
    const val PLACES = "places"
    const val VOTES = "votes"
    const val COMMENTS = "comments"
    const val NOTIFICATIONS = "notifications"
    const val META = "meta"
    const val SEED_DOC = "seed"
}
