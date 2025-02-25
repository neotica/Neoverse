package id.neotica.neoverse.presentation.screen.sharing

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import id.neotica.chat.ChatView
import id.neotica.home.addchat.AddChatView
import id.neotica.neoverse.presentation.screen.sharing.screen.MainSharingScreen
import id.neotica.ui.navigation.Screen

@Composable
fun SharingNavGraph(
    navController: NavHostController,
    chatText: String = ""
) {
    NavHost(
        navController,
        startDestination = SharingScreen.MainSharingView
    ){
        composable<SharingScreen.MainSharingView> { MainSharingScreen(navController = navController) }
        composable<Screen.AddChatScreen> { AddChatView(navController) }
        composable<Screen.Chat> {
            val args = it.toRoute<Screen.Chat>()
            ChatView(
                navController = navController,
                defaultText = chatText,
                args = args
            )
        }
    }
}