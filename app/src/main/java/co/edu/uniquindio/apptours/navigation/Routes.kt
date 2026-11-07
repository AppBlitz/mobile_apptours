package co.edu.uniquindio.apptours.navigation

import kotlinx.serialization.Serializable

// Grafo de autenticación
@Serializable data object AuthGraph
@Serializable data object Welcome
@Serializable data object Login
@Serializable data object Register
@Serializable data object ForgotPassword

// Grafo del usuario
@Serializable data object UserGraph
@Serializable data object UserHome
@Serializable data class PlaceDetail(val placeId: String)
@Serializable data object CreatePlace
@Serializable data class EditPlace(val placeId: String)
@Serializable data object Recommendations
@Serializable data object MyPlaces
@Serializable data object AccountSettings

// Grafo del moderador
@Serializable data object ModeratorGraph
@Serializable data object ModeratorHome
@Serializable data class ModeratorReview(val placeId: String)
