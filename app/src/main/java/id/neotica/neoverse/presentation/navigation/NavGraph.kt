package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import id.neotica.neoverse.presentation.screen.ChatFromHomeView
import id.neotica.neoverse.presentation.screen.ChatView
import id.neotica.neoverse.presentation.screen.HomeView


@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeView(paddingValues) }
        composable(Screen.Chat.route) { ChatView(paddingValues = paddingValues) }
        composable(Screen.Setting.route) { HomeView(paddingValues) }
        composable(Screen.ChatFromHome.route) { ChatFromHomeView(paddingValues = paddingValues) }
        authNavGraph(navController, paddingValues)
    }
}