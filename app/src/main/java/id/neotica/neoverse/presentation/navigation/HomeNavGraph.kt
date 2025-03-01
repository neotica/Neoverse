package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.neotica.chat.ChatView
import id.neotica.home.HomeView
import id.neotica.home.addchat.AddChatView
import id.neotica.home.updateapp.UpdateAppView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    navigation<RootScreen.HomeNav>(
        startDestination = Screen.HomeScreen
    ) {
        composable<Screen.HomeScreen> {
            HomeView(navController, paddingValues)
        }
        composable<Screen.AddChatScreen> {
            AddChatView(navController)
        }
        composable<Screen.Chat>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            val args = it.toRoute<Screen.Chat>()
            ChatView(navController, args)
        }
        composable<Screen.UpdateAppScreen> {
            UpdateAppView()
        }
    }
}