package id.neotica.alexandria.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.alexandria.components.BookItem
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.navigation.AlexandriaScreen

@Composable
fun LibraryView(
    navController: NavController
) {
    BasicScaffold(
        "The Library of Thoth",
        navController = navController,
        enableActionBar = true
    ) {
        BookItem(
            title = "The Bloody Age",
            route = AlexandriaScreen.BloodyAgeScreen,
            navController = navController
        )
        BookItem(
            title = "Goetia",
            route = AlexandriaScreen.GoetiaScreen,
            navController = navController
        )
    }
}