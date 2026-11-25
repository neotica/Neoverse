package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.chat.ChatView
import id.neotica.home.HomeView
import id.neotica.home.addchat.AddChatView
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
            HomeView(navController, paddingValues)
        }
        composable<Screen.AddChatScreen> {
            AddChatView(navController, paddingValues)
        }
        composable<Screen.Conversation> {
            ChatView(navController, paddingValues)
        }
    }
}
