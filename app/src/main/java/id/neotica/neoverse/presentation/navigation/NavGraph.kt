package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import id.neotica.neoverse.presentation.screen.ChatFromHomeView
import id.neotica.neoverse.presentation.screen.SettingView
import id.neotica.neoverse.presentation.screen.SplashView

@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController, startDestination = Screen.Splash) {
        composable<Screen.Splash> { SplashView(navController = navController) }
        composable<Screen.Setting> { SettingView(paddingValues) }
        composable<Screen.ChatFromHome> { ChatFromHomeView(paddingValues = paddingValues) }

        homeNavGraph(navController, paddingValues)
        authNavGraph(navController, paddingValues)
        gameNavGraph(navController, paddingValues)
    }
}