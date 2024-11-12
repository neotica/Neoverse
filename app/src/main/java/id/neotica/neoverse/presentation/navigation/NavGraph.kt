package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.neotica.neoverse.presentation.screen.ChatFromHomeView
import id.neotica.neoverse.presentation.screen.SettingView
import id.neotica.neoverse.presentation.screen.SplashView
import id.neotica.neoverse.presentation.screen.dump.DumpView
import id.neotica.ui.navigation.Screen
import id.neotica.world.worldNavGraph

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
) {
    NavHost(
        navController,
        startDestination = Screen.Splash,
        enterTransition = { fadeIn(tween(100)) },
        popEnterTransition = { EnterTransition.None },
        exitTransition = { fadeOut(tween(100)) },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<Screen.Splash> { SplashView(navController = navController) }
        composable<Screen.Setting> { SettingView(paddingValues) }
        composable<Screen.ChatFromHome> { ChatFromHomeView(paddingValues = paddingValues) }
        composable<Screen.DumpScreen> { DumpView(navController, paddingValues = paddingValues) }

        homeNavGraph(navController, paddingValues)
        authNavGraph(navController, paddingValues)
        worldNavGraph(navController, contentInsets, paddingValues)
        friendNavGraph(navController, paddingValues)
    }
}