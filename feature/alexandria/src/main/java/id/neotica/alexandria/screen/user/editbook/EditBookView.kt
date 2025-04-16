package id.neotica.alexandria.screen.user.editbook

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.utils.convertLongToTime
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.edittext.CustomNeoTextField
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

    var isLoadingChapter by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

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

    var isChecked by remember { mutableStateOf(false) }

    val navBackStackEntry = remember { navController.currentBackStackEntry }

    LaunchedEffect(Unit) {
        val refresh = navBackStackEntry?.savedStateHandle?.get<Boolean>("refresh") == true
        if (refresh) {
            viewModel.getChapters()
            navBackStackEntry.savedStateHandle["refresh"] = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.chapters.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoadingChapter = true
                is ApiResult.Success -> {
                    isLoadingChapter = false
                    chapterList.value = result.data!!
                    isChecked = result.data!!.isPublished
                }
                is ApiResult.Error -> isLoadingChapter = false
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect{
            navController.navigateBack()
        }
    }

    BasicScaffold(
        chapterList.value?.title ?: "",
        isLoading = isLoading,
        fullScreen = true,
        trailingIcon = {
            DotsMenuDropDown(
                item = listOf(
                    DotsMenuItem("Delete Book") { viewModel.deleteBook() }
                ),
            )
        }
    ) {
        Spacer(modifier = Modifier.padding(top = NeoPadding.topBarPadding))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            var title = remember { mutableStateOf("") }
            var chapterTitle = remember { mutableStateOf("") }

            chapterList.value?.let {

                LaunchedEffect(Unit) { title.value = it.title }
                var editTitle by remember { mutableStateOf(false) }
                CustomNeoTextField(
                    value = title,
                    label = "Title",
                    enabled = editTitle,
                    trailingIcon = {
                        Box(
                            Modifier.padding(end = 8.dp)
                        ) { NeoButton(if (!editTitle) "Edit" else "Done") {
                            editTitle = !editTitle
                            if (!editTitle) {
                                val updatedBook = it.copy(
                                    title = title.value,
                                )
                                viewModel.editBook(updatedBook)
                            }
                        } }

                    }
                )

                Text("Author: ${it.author}")
                val createdAt = convertLongToTime(it.createdAt, clean = true)
                Text("Created at $createdAt")
                Row {
                    Text("Published: ")
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            isChecked = checked
                            val updatedBook = it.copy(
                                isPublished = checked
                            )
                            viewModel.editBook(updatedBook)
                        }
                    )
                }
                Text("Chapter title: ")
                NeoTextField(chapterTitle)

                LaunchedEffect(Unit) {
                    viewModel.chapterCreated.collect { chapterResult ->
                        when(chapterResult) {
                            is ApiResult.Success -> {
                                navController.navigate(AlexandriaScreen.EditChapterScreen(
                                    chapterResult.data!!.id, chapterResult.data!!.title)
                                )
                            }
                            else -> {}
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