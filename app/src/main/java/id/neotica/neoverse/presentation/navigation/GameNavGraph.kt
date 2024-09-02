package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.neoverse.presentation.screen.game.HomeGameView

fun NavGraphBuilder.gameNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.GameNav>(
        startDestination = Screen.GameScreen
    ) {
        composable<Screen.GameScreen> { HomeGameView(navController, paddingValues) }
    }
}