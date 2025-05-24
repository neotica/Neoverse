package id.neotica.alexandria.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.alexandria.screen.admin.AdminView
import id.neotica.alexandria.screen.admin.library.AdminLibraryView
import id.neotica.alexandria.screen.admin.library.book.AdminBookView
import id.neotica.alexandria.screen.admin.warning.WarningHubView
import id.neotica.alexandria.screen.admin.warning.detail.WarningDetailView
import id.neotica.alexandria.screen.book.BookView
import id.neotica.alexandria.screen.book.content.BookContentView
import id.neotica.alexandria.screen.library.LibraryView
import id.neotica.alexandria.screen.user.UserLibraryView
import id.neotica.alexandria.screen.user.addbook.AddBookView
import id.neotica.alexandria.screen.user.editbook.EditBookView
import id.neotica.alexandria.screen.user.editbook.chapter.EditChapterView
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.state.AppVariant

fun NavGraphBuilder.alexandriaNavGraph(
    navController: NavController,
    appVariant: AppVariant,
) {
    navigation<RootScreen.AlexandriaNav>(
        startDestination = AlexandriaScreen.HomeScreen
    ) {
        composable<AlexandriaScreen.HomeScreen> {
            LibraryView(navController, appVariant)
        }
        composable<AlexandriaScreen.BookScreen> {
            BookView(navController)
        }
        composable<AlexandriaScreen.UserLibraryScreen> {
            UserLibraryView(navController)
        }
        composable<AlexandriaScreen.AddBookScreen> {
            AddBookView(navController)
        }
        composable<AlexandriaScreen.EditBookScreen> {
            EditBookView(navController)
        }
        composable<AlexandriaScreen.EditChapterScreen> {
            EditChapterView(navController)
        }
        composable<AlexandriaScreen.BookContentScreen> {
            BookContentView(navController)
        }
        composable<AlexandriaScreen.AdminLibraryScreen> {
            AdminLibraryView(navController)
        }
        composable<AlexandriaScreen.AdminBookScreen> {
            AdminBookView(navController)
        }
        composable<AlexandriaScreen.WarningHubScreen> {
            WarningHubView(navController)
        }
        composable<AlexandriaScreen.WarningDetailScreen> {
            WarningDetailView(navController)
        }
        composable<AlexandriaScreen.AdminScreen> { AdminView(navController) }
    }
}