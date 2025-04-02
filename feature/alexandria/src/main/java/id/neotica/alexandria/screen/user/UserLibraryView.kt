package id.neotica.alexandria.screen.user

import android.util.Log
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
import id.neotica.domain.model.alexandria.Book
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.AlexandriaScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserLibraryView(
    navController: NavController,
    viewModel: UserLibraryViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf(emptyList<Book>()) }
    var reload by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.books.collect { result ->
            when (result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    books = result.data!!
                }
                is ApiResult.Error -> {
                    isLoading = false
                    viewModel.getBooks()
                    Log.e("library", result.errorMessage.toString())
                }
            }
        }
    }

    BasicScaffold(
        "User Library",
        navController = navController,
        enableActionBar = true
    ) {
        LazyColumn {
            item {
                if (reload) NeoButton("Reload") {
                    reload = false
                    isLoading = true
                    viewModel.getBooks()
                }
                books
                    .sortedBy { it.createdAt }
                    .forEach {
                    BookItem(
                        title = it.title,
                        route = AlexandriaScreen.EditBookScreen(url = it.id),
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