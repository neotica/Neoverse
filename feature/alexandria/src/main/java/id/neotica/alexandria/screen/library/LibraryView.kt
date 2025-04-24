package id.neotica.alexandria.screen.library

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.alexandria.components.BookItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryView(
    navController: NavController,
    viewModel: LibraryViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf(emptyList<Book>()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.books.collect { result ->
            when (result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    books = result.data!!.content
                }
                is ApiResult.Error -> {
                    isLoading = false
                    Log.e("library", result.errorMessage.toString())
                }
            }
        }
    }

    BasicScaffold(
        "The Library of Thoth",
        navController = navController,
        enableActionBar = true,
        trailingIcon = {
            var gotoAuthor by remember { mutableStateOf(false) }
            var gotoAdmin by remember { mutableStateOf(false) }

            LaunchedEffect(gotoAuthor, gotoAdmin) {
                when {
                    gotoAuthor -> {
                        viewModel.permission.collectLatest {
                            gotoAuthor = false
                            if (it) {
                                navController.navigate(AlexandriaScreen.UserLibraryScreen)
                            } else {
                                Toast.makeText(context, "You did not have an author's account.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    gotoAdmin -> {
                        viewModel.permission.collectLatest {
                            gotoAdmin = false
                            if (it) {
                                navController.navigate(AlexandriaScreen.AdminScreen)
                            } else {
                                Toast.makeText(context, "You did not have an admin's account.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Author's Page") { gotoAuthor = true },
                    DotsMenuItem("Admin Page") { gotoAdmin = true }
                ),
            )
        }
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
    if (isLoading) Box(
        Modifier
            .fillMaxSize()
            .clickable(enabled = true) { },
    ) { LoadingIndicator() }
}