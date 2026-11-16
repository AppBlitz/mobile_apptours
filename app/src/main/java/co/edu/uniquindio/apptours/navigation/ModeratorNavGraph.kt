package co.edu.uniquindio.apptours.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import co.edu.uniquindio.apptours.features.moderator.home.ModeratorHomeRoute
import co.edu.uniquindio.apptours.features.moderator.review.ModeratorReviewRoute

fun NavGraphBuilder.moderatorGraph(navController: NavController) {
    navigation<ModeratorGraph>(startDestination = ModeratorHome) {
        composable<ModeratorHome> {
            ModeratorHomeRoute(
                onOpenReview = { placeId -> navController.navigate(ModeratorReview(placeId)) },
                onLoggedOut = { navController.navigateClearingBackStack(Login) }
            )
        }

        composable<ModeratorReview> {
            ModeratorReviewRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
