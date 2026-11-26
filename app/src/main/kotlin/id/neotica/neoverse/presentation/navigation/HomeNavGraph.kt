package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.chat.ChatRoute
import id.neotica.home.HomeRoute
import id.neotica.home.addchat.AddChatRoute
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    navigation<RootScreen.HomeNav>(
        startDestination = Screen.HomeScreen,
    ) {
        composable<Screen.HomeScreen> {
            HomeRoute(navController, paddingValues)
        }
        composable<Screen.AddChatScreen> {
            AddChatRoute(navController, paddingValues)
        }
        composable<Screen.Conversation> {
            ChatRoute(navController, paddingValues)
        }
    }
}
