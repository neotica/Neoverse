package id.neotica.alexandria.screen.user.editbook

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.utils.convertLongToTime
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.navigateBack
import id.neotica.ui.theme.NeoPadding
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookView(
    navController: NavController,
    viewModel: EditBookViewModel = koinViewModel()
) {
    val chapterList = remember { mutableStateOf<Book?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChapter by remember { mutableStateOf(false) }
    var isLoadingChapter2 by remember { mutableStateOf(false) }


    val searchBar = remember { mutableStateOf("") }

    val context = LocalContext.current

    var chapter by remember {
        mutableStateOf(
            Chapter(
                id = 0,
                title = "",
                bookId = "",
                createdAt = 0,
                modifiedAt = 0,
            )
        )
    }
    var chapterDetail by remember { mutableStateOf<PaginatedContent?>(null) }
    var chapterDetailStat by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        viewModel.chapters.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    chapterList.value = result.data!!
                }
                is ApiResult.Error -> isLoading = false
            }
        }
    }

    LaunchedEffect(chapter.id) {
        Log.d("EditBookView", "Fetching chapter details for id: ${chapter.id}")
    }

    LaunchedEffect(Unit) {
        viewModel.editContent.collect {
            when (it) {
                is ApiResult.Loading -> isLoadingChapter2 = true
                is ApiResult.Success -> {
                    isLoadingChapter2 = false
                    Toast.makeText(context, it.data, Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> isLoadingChapter2 = false
            }
        }
    }

    BasicScaffold(
        chapterList.value?.title ?: "",
        fullScreen = true,
        trailingIcon = {
            LaunchedEffect(Unit) {
                viewModel.bookDeleted.collect { result ->
                    when (result) {
                        is ApiResult.Loading -> {
                            chapterDetail = null
                            isLoadingChapter2 = true
                        }
                        is ApiResult.Success -> {
                            isLoadingChapter2 = false
                            Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                            navController.navigateBack()
                        }
                        is ApiResult.Error -> {
                            isLoadingChapter2 = false
                            chapterDetailStat = result.errorMessage.toString()
                        }
                    }
                }
            }

            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Delete Book") { viewModel.deleteBook() }
                ),
            )
        }
    ) {
        if (isLoading) LoadingIndicator()
        Spacer(modifier = Modifier.padding(top = NeoPadding.topBarPadding))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            var title = remember { mutableStateOf("") }
            var chapterTitle = remember { mutableStateOf("") }

            chapterList.value?.let {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Title:")
                    NeoTextField(value = title, label = it.title)
                }

                Text("Author: ${it.author}")
                val createdAt = convertLongToTime(it.createdAt, clean = true)
                Text("Created at $createdAt")
                NeoButton(
                    "Change title",
                    enabled = title.value.isNotEmpty()
                ) {
                    val updatedBook = it.copy(
                        title = title.value
                    )
                    viewModel.editBook(updatedBook)
                }
                Text("Chapter title: ")
                NeoTextField(chapterTitle)

                LaunchedEffect(Unit) {
                    viewModel.chapterCreated.collect { chapterResult ->
                        when(chapterResult) {
                            is ApiResult.Loading -> {
                                isLoadingChapter = true
                            }
                            is ApiResult.Success -> {
                                isLoadingChapter = false
                                navController.navigate(AlexandriaScreen.EditChapterScreen(
                                    chapterResult.data!!.id, chapterResult.data!!.title)
                                )
                                Toast.makeText(context, "Chapter created.", Toast.LENGTH_SHORT).show()
                            }
                            is ApiResult.Error -> {
                                isLoadingChapter = false
                                Toast.makeText(context, chapterResult.errorMessage.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                NeoButton(
                    "Create chapter",
                    enabled = chapterTitle.value.isNotEmpty(),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    viewModel.createChapter(
                        ChapterRequest(
                            title = chapterTitle.value,
                            bookId = it.id
                        )
                    )
                }
            }


//            NeoSearchBar(searchBar, label = "Search Chapters")
        }


        LazyColumn(
            contentPadding = PaddingValues(
                bottom = NeoPadding.systemBarPadding
            )
        ) {
            val spiritsFilter = chapterList.value?.chapters?.filter {
                it.title.contains(searchBar.value, ignoreCase = true)
            }
            if (isLoadingChapter) {
                item { LoadingIndicator() }
            } else {
                spiritsFilter?.let { chap ->
                    items(chap) { item ->
                        ChapterItem(
                            title = item.title,
                        ) {
                            navController.navigate(AlexandriaScreen.EditChapterScreen(
                                item.id, item.title)
                            )
                            chapter = Chapter(
                                id = item.id,
                                title = item.title,
                                bookId = item.bookId,
                                createdAt = item.createdAt,
                                modifiedAt = item.modifiedAt,
                            )
                        }
                    }
                }
            }
        }
    }
}