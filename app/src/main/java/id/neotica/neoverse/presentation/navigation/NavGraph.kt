package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import id.neotica.neoverse.presentation.screen.ChatFromHomeView
import id.neotica.neoverse.presentation.screen.ChatView
import id.neotica.neoverse.presentation.screen.HomeView
import id.neotica.neoverse.presentation.screen.SettingView
import id.neotica.neoverse.presentation.screen.SplashView


@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashView(navController = navController) }
        composable(RootScreen.HomeNav.route) { HomeView(navController, paddingValues) }
        composable(Screen.Chat.route) { ChatView(paddingValues = paddingValues) }
        composable(Screen.Setting.route) { SettingView(paddingValues) }
        composable(Screen.ChatFromHome.route) { ChatFromHomeView(paddingValues = paddingValues) }
        authNavGraph(navController, paddingValues)
    }
}