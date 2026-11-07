package co.edu.uniquindio.apptours.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import co.edu.uniquindio.apptours.features.user.account.AccountSettingsRoute
import co.edu.uniquindio.apptours.features.user.home.UserHomeRoute
import co.edu.uniquindio.apptours.features.user.placedetail.PlaceDetailRoute
import co.edu.uniquindio.apptours.features.user.placeform.PlaceFormRoute

fun NavGraphBuilder.userGraph(navController: NavController) {
    navigation<UserGraph>(startDestination = UserHome) {
        composable<UserHome> {
            UserHomeRoute(
                onOpenPlaceDetail = { placeId -> navController.navigate(PlaceDetail(placeId)) },
                onOpenCreatePlace = { navController.navigate(CreatePlace) },
                onOpenAccountSettings = { navController.navigate(AccountSettings) },
                onLoggedOut = { navController.navigateClearingBackStack(Login) }
            )
        }

        composable<PlaceDetail> {
            PlaceDetailRoute(
                onNavigateBack = { navController.popBackStack() },
                onEditPlace = { placeId -> navController.navigate(EditPlace(placeId)) }
            )
        }

        composable<CreatePlace> {
            PlaceFormRoute(
                onNavigateBack = { navController.popBackStack() },
                onPlaceDeleted = { navController.popBackStack(UserHome, inclusive = false) }
            )
        }

        composable<EditPlace> {
            PlaceFormRoute(
                onNavigateBack = { navController.popBackStack() },
                onPlaceDeleted = { navController.popBackStack(UserHome, inclusive = false) }
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
