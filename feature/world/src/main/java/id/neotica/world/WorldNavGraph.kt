package id.neotica.world

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.world.feat.game.GameView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.world.feat.kanye.KanyeView
import id.neotica.world.feat.market.MarketView
import id.neotica.world.feat.world.WorldView

fun NavGraphBuilder.worldNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.WorldNav>(
        startDestination = Screen.WorldScreen
    ) {
        composable<Screen.WorldScreen> {
            WorldView(
                navController,
                paddingValues
            )
        }
        composable<Screen.GameScreen> {
            GameView(
                navController, paddingValues
            )
        }
        composable<Screen.KanyeScreen> {
            KanyeView(
                navController, paddingValues
            )
        }
        composable<Screen.MarketScreen> {
            MarketView(
                navController, paddingValues
            )
        }
    }
}