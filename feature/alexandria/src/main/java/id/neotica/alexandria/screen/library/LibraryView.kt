package id.neotica.alexandria.screen.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import id.neotica.alexandria.components.BookItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Books
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.navigation.AlexandriaScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryView(
    navController: NavController,
    viewModel: LibraryViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf(emptyList<Books>()) }

    LaunchedEffect(Unit) {
        viewModel.books.collect { result ->
            when (result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    books = result.data!!
                }
                is ApiResult.Error -> isLoading = false
            }

        }
    }
    BasicScaffold(
        "The Library of Thoth",
        navController = navController,
        enableActionBar = true
    ) {
        LazyColumn {
            item {
                books.forEach {
                    BookItem(
                        title = it.name,
                        route = AlexandriaScreen.BookScreen(url = it.url),
                        navController = navController
                    )
                }
                if (!isLoading) {
                    BookItem(
                        title = "Goetia",
                        route = AlexandriaScreen.GoetiaScreen,
                        navController = navController
                    )
                }
            }
        }
    }
    if (isLoading) Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = true) { },
    ) { LoadingIndicator() }
}