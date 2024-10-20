package id.neotica.neoversewear.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import id.neotica.neoversewear.presentation.screen.auth.WearLoginView
import id.neotica.neoversewear.presentation.screen.chat.WChatView
import id.neotica.neoversewear.presentation.screen.home.WearHomeNavView
import id.neotica.neoversewear.presentation.screen.home.WearHomeScreen
import id.neotica.neoversewear.presentation.screen.profile.WProfileView

@Composable
fun WearNavGraph(navController: NavHostController) {
    SwipeDismissableNavHost(navController = navController, startDestination = WearScreen.HomeNavScreen.toString()) {
        composable(WearScreen.HomeNavScreen.toString()) {
            WearHomeNavView(navController)
        }
        composable(WearScreen.HomeScreen.toString()) {
            WearHomeScreen(navController)
        }
        composable(WearScreen.LoginScreen.toString()) { WearLoginView(navController) }
        composable(WearScreen.ProfileScreen.toString()) {
            WProfileView(navController)
        }
        composable("WearScreen.ChatScreen/{chatId}/{user2}/{chatroomId}") {
            val chatId = it.arguments?.getString("chatId")
            val user2 =  it.arguments?.getString("user2")
            val chatroomId = it.arguments?.getString("chatroomId")
            val args = WearScreen.ChatScreen(chatId!!, user2!!, chatroomId)
            WChatView(navController, args)
        }
    }
}