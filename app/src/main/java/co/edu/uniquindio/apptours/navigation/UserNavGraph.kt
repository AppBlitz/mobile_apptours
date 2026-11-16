package co.edu.uniquindio.apptours.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import co.edu.uniquindio.apptours.features.user.account.AccountSettingsRoute
import co.edu.uniquindio.apptours.features.user.home.UserHomeRoute
import co.edu.uniquindio.apptours.features.user.myplaces.MyPlacesRoute
import co.edu.uniquindio.apptours.features.user.placedetail.PlaceDetailRoute
import co.edu.uniquindio.apptours.features.user.placeform.PlaceFormRoute
import co.edu.uniquindio.apptours.features.user.recommendations.RecommendationsRoute

fun NavGraphBuilder.userGraph(navController: NavController) {
    navigation<UserGraph>(startDestination = UserHome) {
        composable<UserHome> {
            UserHomeRoute(
                onOpenPlaceDetail = { placeId -> navController.navigate(PlaceDetail(placeId)) },
                onOpenCreatePlace = { navController.navigate(CreatePlace) },
                onOpenMyPlaces = { navController.navigate(MyPlaces) },
                onOpenRecommendations = { navController.navigate(Recommendations) },
                onOpenAccountSettings = { navController.navigate(AccountSettings) },
                onLoggedOut = { navController.navigateClearingBackStack(Login) }
            )
        }

        composable<PlaceDetail> {
            PlaceDetailRoute(
                onNavigateBack = { navController.popBackStack() },
                onEditPlace = { placeId -> navController.navigate(EditPlace(placeId)) },
                onPlaceDeleted = { navController.popBackStack() }
            )
        }

        composable<CreatePlace> {
            PlaceFormRoute(
                onNavigateBack = { navController.popBackStack() },
                onPlaceDeleted = { navController.popBackStack(UserHome, inclusive = false) },
                // Se descarta el borrador: el usuario va a comentar en el lugar que ya existía.
                onOpenExistingPlace = { placeId ->
                    navController.navigate(PlaceDetail(placeId, focusComments = true)) {
                        popUpTo<CreatePlace> { inclusive = true }
                    }
                }
            )
        }

        // Al borrar desde la edición se sale también del detalle (la publicación ya no existe).
        composable<EditPlace> {
            PlaceFormRoute(
                onNavigateBack = { navController.popBackStack() },
                onPlaceDeleted = {
                    if (!navController.popBackStack<PlaceDetail>(inclusive = true)) navController.popBackStack()
                },
                onOpenExistingPlace = { placeId -> navController.navigate(PlaceDetail(placeId, focusComments = true)) }
            )
        }

        composable<MyPlaces> {
            MyPlacesRoute(
                onNavigateBack = { navController.popBackStack() },
                onOpenPlace = { placeId -> navController.navigate(PlaceDetail(placeId)) }
            )
        }

        composable<Recommendations> {
            RecommendationsRoute(
                onNavigateBack = { navController.popBackStack() },
                onOpenPlace = { placeId -> navController.navigate(PlaceDetail(placeId)) }
            )
        }

        composable<AccountSettings> {
            AccountSettingsRoute(
                onNavigateBack = { navController.popBackStack() },
                onAccountDeleted = { navController.navigateClearingBackStack(Login) }
            )
        }
    }
}
