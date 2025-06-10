package id.neotica.alexandria.screen.library

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.alexandria.components.BookItem
import id.neotica.domain.model.alexandria.Book
import id.neotica.routes.AlexandriaScreen

@Composable
fun LibraryChildView(
    navController: NavController,
    books: List<Book>,
) {
    LazyColumn {
        item {
            books.forEach {
                BookItem(
                    title = it.title,
                    route = AlexandriaScreen.BookScreen(id = it.id),
                    navController = navController
                )
            }
        }
    }
}