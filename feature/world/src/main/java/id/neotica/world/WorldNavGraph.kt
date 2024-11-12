package id.neotica.world

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.world.feat.game.GameView
import id.neotica.world.feat.inventory.InventoryView
import id.neotica.world.feat.kanye.KanyeView
import id.neotica.world.feat.market.MarketView
import id.neotica.world.feat.market.itemdetail.ItemDetailView
import id.neotica.world.feat.market.merchant.MerchantView
import id.neotica.world.feat.market.merchant.hero.HeroMakerView
import id.neotica.world.feat.market.merchant.item.ItemMakerView
import id.neotica.world.feat.mining.MiningView
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
        composable<WorldScreen.GameScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            GameView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.KanyeScreen> {
            KanyeView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.MarketScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            MarketView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.ItemDetailScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            ItemDetailView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.MerchantScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            MerchantView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.InventoryScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            InventoryView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.MiningScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            MiningView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.HeroMakerScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            HeroMakerView(
                navController, paddingValues, contentInsets
            )
        }
        composable<WorldScreen.ItemMakerScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            ItemMakerView(
                navController, paddingValues, contentInsets
            )
        }
    }
}