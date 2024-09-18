package id.neotica.world.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.world.game.HomeGameView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen

fun NavGraphBuilder.gameNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.GameNav>(
        startDestination = Screen.GameScreen
    ) {
        composable<Screen.GameScreen> {
            HomeGameView(
                navController,
                paddingValues
            )
        }
    }
}