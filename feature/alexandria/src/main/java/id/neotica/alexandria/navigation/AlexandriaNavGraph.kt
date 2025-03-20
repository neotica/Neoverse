package id.neotica.alexandria.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.alexandria.screen.book.BookView
import id.neotica.alexandria.screen.library.LibraryView
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.RootScreen

fun NavGraphBuilder.alexandriaNavGraph(navController: NavController) {
    navigation<RootScreen.AlexandriaNav>(
        startDestination = AlexandriaScreen.HomeScreen
    ) {
        composable<AlexandriaScreen.HomeScreen> {
            LibraryView(navController)
        }
        composable<AlexandriaScreen.BookScreen> {
            BookView(navController)
        }
    }
}