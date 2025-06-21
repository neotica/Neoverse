package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.neotica.neoverse.presentation.screen.SettingView
import id.neotica.neoverse.presentation.screen.SplashView
import id.neotica.routes.Screen
import id.neotica.state.AppVariant
import id.neotica.world.worldNavGraph
import org.koin.compose.getKoin

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
) {
    val appVariant: AppVariant = getKoin().get()

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

        homeNavGraph(navController, paddingValues)
        profileNavGraph(navController, paddingValues, appVariant)
        worldNavGraph(navController, paddingValues, appVariant)
        friendNavGraph(navController, paddingValues)
    }
}