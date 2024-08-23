package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.neoverse.presentation.screen.ChatView
import id.neotica.neoverse.presentation.screen.home.HomeView

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.HomeNav>(
        startDestination = Screen.HomeScreen
    ) {
        composable<Screen.HomeScreen> {
            HomeView(navController, paddingValues)
        }
        composable<Screen.Chat> { ChatView(paddingValues = paddingValues) }
    }
}