package id.neotica.alexandria.screen.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.BookItem
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.routes.AlexandriaScreen
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.navigation.onRefresh
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserLibraryView(
    navController: NavController,
    viewModel: UserLibraryViewModel = koinViewModel()
) {
    val isLoading by viewModel.loading.collectAsState()
    val books by viewModel.books.collectAsState()
    var reload by remember { mutableStateOf(false) }
    val message by viewModel.message.collectAsState()

    var toast = remember { mutableStateOf(false) }
    var toastMessage = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        navController.onRefresh { viewModel.getBooks() }
    }

    LaunchedEffect(message) {
        message?.let {
            toast.value = true
            toastMessage.value = it
        }
    }

    BasicScaffold(
        "Author's Page",
        navController = navController,
        enableActionBar = true
    ) {
        LazyColumn {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (reload) NeoButton("Reload") {
                        reload = false
                        viewModel.getBooks()
                    }
                    NeoButton("Add New Book") {
                        navController.navigate(AlexandriaScreen.AddBookScreen)
                    }
                }
            }
            item {
                books
                    .sortedBy { it.createdAt }
                    .forEach {
                    BookItem(
                        title = it.title,
                        route = AlexandriaScreen.EditBookScreen(id = it.id),
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
    if (toast.value) NeoToast(toastMessage.toString(), toast)
}