package id.neotica.neoverse.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashRoute(
    navController: NavController,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SplashEvent.NavigateHome -> navController.navigate(RootScreen.HomeNav) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                SplashEvent.NavigateLogin -> navController.navigate(Screen.LoginScreen) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    SplashView(state)
}
