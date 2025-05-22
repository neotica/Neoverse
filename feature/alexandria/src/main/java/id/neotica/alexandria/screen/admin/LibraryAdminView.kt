package id.neotica.alexandria.screen.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.utils.convertLongToTime
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.theme.DarkPrimaryTransparent80
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryAdminView(
    navController: NavController,
    viewModel: LibraryAdminViewModel = koinViewModel()
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
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isLoading.collect {
            isLoading = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.permission.collect {
            if (it == false) {
                navController.navigateUp()
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
                        Column(
                            modifier = Modifier
                                .clickable { AlexandriaScreen.BookScreen(id = it.id) }
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = it.title,
//                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text("Author: ${it.author}")
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var isChecked by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) { isChecked = it.isPublished }


                                Text("Published: ")
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        isChecked = checked
                                        val request = BookRequest(
                                            title = it.title,
                                            author = it.author,
                                            language = it.language,
                                            isPublished = checked,
                                        )
                                        viewModel.publish(it.id, request)
                                    }
                                )
                            }
                            Text("Language: ${it.language}")
                            Text("Created at: ${ convertLongToTime(time = it.createdAt) }")
                            Text("Genre: ${it.genre?: "N/A"}")
//                            Text("Summary: ${it.summary}")
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