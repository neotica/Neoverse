package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.neoverse.presentation.screen.auth.LoginView
import id.neotica.neoverse.presentation.screen.auth.RegisterView

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
){
    navigation(
        startDestination = Screen.Login.route,
        route = RootScreen.AuthNav.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginView(navController, paddingValues = paddingValues)
        }
        composable(
            route = Screen.Register.route
        ) {
            RegisterView(navController, paddingValues = paddingValues)
        }
    }
}