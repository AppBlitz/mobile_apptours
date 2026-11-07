package co.edu.uniquindio.apptours.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import co.edu.uniquindio.apptours.core.theme.ColabDarkGreen

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val startDestination by sessionViewModel.startDestination.collectAsStateWithLifecycle()
    val start = startDestination
    if (start == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ColabDarkGreen)
        }
        return
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = start) {
        authGraph(navController)
        userGraph(navController)
        moderatorGraph(navController)
    }
}

/** Navega a [route] vaciando la pila (login, logout, eliminar cuenta). */
fun NavController.navigateClearingBackStack(route: Any) {
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
