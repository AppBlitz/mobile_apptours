package co.edu.uniquindio.apptours.features.principal

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.features.login.ForgotPasswordScreen
import co.edu.uniquindio.apptours.features.login.LoginScreen
import co.edu.uniquindio.apptours.features.login.LoginViewModel
import co.edu.uniquindio.apptours.features.login.WelcomeScreen
import co.edu.uniquindio.apptours.features.register.RegisterScreen
import co.edu.uniquindio.apptours.features.register.RegisterViewModel

object ColabRoutes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val USER_MAIN = "user_main"
    const val PLACE_DETAIL = "place_detail/{placeId}"
    const val CREATE_PLACE = "create_place"
    const val EDIT_PLACE = "edit_place/{placeId}"
    const val ACCOUNT_SETTINGS = "account_settings"
    const val MODERATOR_MAIN = "moderator_main"
    const val MODERATOR_REVIEW = "moderator_review/{placeId}"

    fun placeDetail(placeId: String) = "place_detail/$placeId"
    fun editPlace(placeId: String) = "edit_place/$placeId"
    fun moderatorReview(placeId: String) = "moderator_review/$placeId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val principalViewModel: PrincipalViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = ColabRoutes.WELCOME
    ) {
        composable(ColabRoutes.WELCOME) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(ColabRoutes.LOGIN) {
                        popUpTo(ColabRoutes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(ColabRoutes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { user ->
                    val target = if (user.role == UserRole.MODERATOR) {
                        ColabRoutes.MODERATOR_MAIN
                    } else {
                        ColabRoutes.USER_MAIN
                    }
                    navController.navigate(target) {
                        popUpTo(ColabRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(ColabRoutes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ColabRoutes.FORGOT_PASSWORD)
                }
            )
        }

        composable(ColabRoutes.REGISTER) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate(ColabRoutes.USER_MAIN) {
                        popUpTo(ColabRoutes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(ColabRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ColabRoutes.USER_MAIN) {
            UserMainScreen(
                viewModel = principalViewModel,
                onOpenPlaceDetail = { placeId ->
                    navController.navigate(ColabRoutes.placeDetail(placeId))
                },
                onOpenCreatePlace = {
                    navController.navigate(ColabRoutes.CREATE_PLACE)
                },
                onOpenAccountSettings = {
                    navController.navigate(ColabRoutes.ACCOUNT_SETTINGS)
                },
                onLogout = {
                    principalViewModel.logout()
                    navController.navigate(ColabRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = ColabRoutes.PLACE_DETAIL,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId").orEmpty()
            PlaceDetailScreen(
                placeId = placeId,
                viewModel = principalViewModel,
                onNavigateBack = { navController.popBackStack() },
                onEditPlace = { id ->
                    navController.navigate(ColabRoutes.editPlace(id))
                }
            )
        }

        composable(ColabRoutes.CREATE_PLACE) {
            CreateEditPlaceScreen(
                placeId = null,
                viewModel = principalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ColabRoutes.EDIT_PLACE,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")
            CreateEditPlaceScreen(
                placeId = placeId,
                viewModel = principalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ColabRoutes.ACCOUNT_SETTINGS) {
            AccountSettingsScreen(
                viewModel = principalViewModel,
                onNavigateBack = { navController.popBackStack() },
                onAccountDeleted = {
                    navController.navigate(ColabRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(ColabRoutes.MODERATOR_MAIN) {
            ModeratorMainScreen(
                viewModel = principalViewModel,
                onOpenReviewDetail = { placeId ->
                    navController.navigate(ColabRoutes.moderatorReview(placeId))
                },
                onLogout = {
                    principalViewModel.logout()
                    navController.navigate(ColabRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = ColabRoutes.MODERATOR_REVIEW,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId").orEmpty()
            ModeratorReviewDetailScreen(
                placeId = placeId,
                viewModel = principalViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
