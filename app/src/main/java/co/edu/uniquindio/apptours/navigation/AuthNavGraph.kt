package co.edu.uniquindio.apptours.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import co.edu.uniquindio.apptours.domain.model.UserRole
import co.edu.uniquindio.apptours.features.auth.forgotpassword.ForgotPasswordRoute
import co.edu.uniquindio.apptours.features.auth.login.LoginRoute
import co.edu.uniquindio.apptours.features.auth.register.RegisterRoute
import co.edu.uniquindio.apptours.features.auth.welcome.WelcomeScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthGraph>(startDestination = Welcome) {
        composable<Welcome> {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo<Welcome> { inclusive = true }
                    }
                }
            )
        }

        composable<Login> {
            LoginRoute(
                onLoginSuccess = { role ->
                    val graph = if (role == UserRole.MODERATOR) ModeratorGraph else UserGraph
                    navController.navigateClearingBackStack(graph)
                },
                onNavigateToRegister = { navController.navigate(Register) },
                onNavigateToForgotPassword = { navController.navigate(ForgotPassword) }
            )
        }

        composable<Register> {
            RegisterRoute(
                onRegisterSuccess = { navController.navigateClearingBackStack(UserGraph) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable<ForgotPassword> {
            ForgotPasswordRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
