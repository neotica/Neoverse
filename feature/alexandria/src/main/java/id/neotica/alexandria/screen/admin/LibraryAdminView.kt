package id.neotica.alexandria.screen.admin

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.screen.library.LibraryViewModel
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.navigateBack
import id.neotica.ui.theme.DarkPrimaryTransparent80
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryAdminView(
    navController: NavController,
    viewModel: LibraryViewModel = koinViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var books by remember { mutableStateOf(emptyList<Book>()) }
    val context = LocalContext.current

    var isChecked by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.permission.collectLatest {
            if (!it) {
                navController.navigateBack()
            }
        }
    }

    BasicScaffold(
        "Alexandria Administrator",
        navController = navController,
        enableActionBar = true
    ) {
        LazyColumn {
            item {
                books.forEach {
                    Card(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                        ,
                        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { AlexandriaScreen.BookScreen(id = it.id) }
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it.title,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Published: ")
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        isChecked = checked
                                        val updatedBook = it.copy(
                                            isPublished = checked
                                        )
//                                        viewModel.editBook(updatedBook)
                                    }
                                )
                            }
                        }
                    }
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