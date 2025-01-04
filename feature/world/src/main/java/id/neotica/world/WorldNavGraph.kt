package id.neotica.world

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.raven.RavenChatView
import id.neotica.raven.RavenView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.WorldScreen
import id.neotica.world.feat.game.TavernView
import id.neotica.world.feat.inventory.InventoryView
import id.neotica.world.feat.kanye.KanyeView
import id.neotica.world.feat.leaderboards.LeaderBoardsView
import id.neotica.world.feat.market.MarketView
import id.neotica.world.feat.market.itemdetail.ItemDetailView
import id.neotica.world.feat.market.merchant.MerchantView
import id.neotica.world.feat.market.merchant.hero.HeroMakerView
import id.neotica.world.feat.market.merchant.item.ItemMakerView
import id.neotica.world.feat.mining.MiningView
import id.neotica.world.feat.superadmin.SuperAdminView
import id.neotica.world.feat.terminal.TerminalView
import id.neotica.world.feat.world.WorldView

fun NavGraphBuilder.worldNavGraph(
    navController: NavController,
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
        composable<WorldScreen.TavernScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            TavernView(navController)
        }
        composable<WorldScreen.KanyeScreen> {
            KanyeView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.MarketScreen>(
            enterTransition = { fadeIn(tween(100)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) },
        ) {
            MarketView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.ItemDetailScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            ItemDetailView(navController)
        }
        composable<WorldScreen.MerchantScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            MerchantView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.InventoryScreen>(
            enterTransition = { fadeIn(tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            InventoryView(navController)
        }
        composable<WorldScreen.MiningScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            MiningView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.HeroMakerScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            HeroMakerView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.ItemMakerScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            ItemMakerView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.LeaderBoardsScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            LeaderBoardsView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.TerminalScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            TerminalView(navController)
        }
        composable<WorldScreen.SuperAdminScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            SuperAdminView(
                navController, paddingValues
            )
        }
        composable<WorldScreen.RavenScreen>(
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(100)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(100)) }
        ) {
            RavenView(
                navController
            )
        }
        composable<WorldScreen.RavenChatScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            RavenChatView(
                navController
            )
        }
    }
}