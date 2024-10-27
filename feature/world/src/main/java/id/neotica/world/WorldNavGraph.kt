package id.neotica.world

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.world.feat.game.GameView
import id.neotica.world.feat.kanye.KanyeView
import id.neotica.world.feat.market.MarketView
import id.neotica.world.feat.market.itemdetail.ItemDetailView
import id.neotica.world.feat.market.merchant.MerchantView
import id.neotica.world.feat.world.WorldView

fun NavGraphBuilder.worldNavGraph(
    navController: NavController,
    contentInsets: WindowInsets,
    paddingValues: PaddingValues
) {
    navigation<RootScreen.WorldNav>(
        startDestination = WorldScreen.HomeScreen
    ) {
        composable<WorldScreen.HomeScreen> {
            WorldView(
                navController,
                paddingValues
            )
        }
        composable<WorldScreen.GameScreen> {
            GameView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.KanyeScreen> {
            KanyeView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.MarketScreen> {
            MarketView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.ItemDetailScreen> {
            ItemDetailView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.MerchantScreen> {
            MerchantView(
                navController, paddingValues, contentInsets
            )
        }
    }
}