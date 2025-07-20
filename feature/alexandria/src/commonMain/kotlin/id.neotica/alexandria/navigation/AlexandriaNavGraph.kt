package id.neotica.alexandria.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
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
import id.neotica.alexandria.screen.user.AuthorView
import id.neotica.alexandria.screen.user.addbook.AddBookView
import id.neotica.alexandria.screen.user.editbook.EditBookView
import id.neotica.alexandria.screen.user.editbook.chapter.EditChapterView
import id.neotica.routes.AlexandriaScreen
import id.neotica.routes.RootScreen
import id.neotica.state.AppVariant

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.alexandriaNavGraph(
    navController: NavController,
    appVariant: AppVariant,
) {
    navigation<RootScreen.AlexandriaNav>(
        startDestination = AlexandriaScreen.HomeScreen
    ) {
        composable<AlexandriaScreen.HomeScreen> {
            LibraryView(
                navController = navController,
                appVariant = appVariant,
            )
        }
        composable<AlexandriaScreen.BookScreen> {
            BookView(navController)
        }
        composable<AlexandriaScreen.UserLibraryScreen> {
            AuthorView(navController)
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