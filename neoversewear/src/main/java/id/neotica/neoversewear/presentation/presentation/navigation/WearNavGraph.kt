package id.neotica.neoversewear.presentation.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import id.neotica.neoversewear.presentation.presentation.screen.WearHomeNavView
import id.neotica.neoversewear.presentation.presentation.screen.WearHomeScreen

@Composable
fun WearNavGraph(navController: NavHostController) {
    SwipeDismissableNavHost(navController = navController, startDestination = WearScreen.HomeNavScreen.toString()) {
        composable(WearScreen.HomeNavScreen.toString()) {
            WearHomeNavView(navController)
        }
        composable(WearScreen.HomeScreen.toString()) {
            WearHomeScreen()
        }
    }
}