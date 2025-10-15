package id.neotica.alexandria.screen.user.editbook

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.utils.convertLongToTime
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.topbar.DotsMenuDropDown
import id.neotica.ui.shared.components.topbar.DotsMenuItem
import id.neotica.routes.AlexandriaScreen.EditChapterScreen
import id.neotica.ui.shared.navigation.navigateBack
import id.neotica.ui.shared.navigation.onRefresh
import id.neotica.ui.shared.navigation.refreshOnBack
import id.neotica.ui.shared.theme.NeoPadding
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookView(
    navController: NavController,
    viewModel: EditBookViewModel = koinViewModel()
) {
    val chapterList = viewModel.book.collectAsState()

    var isLoadingChapter by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

    val searchBar = remember { mutableStateOf("") }

    val toast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }

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

    LaunchedEffect(Unit) {
        navController.onRefresh { viewModel.getChapters() }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect {
            toast.value = true
            toastMessage.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect{
            navController.refreshOnBack()
            navController.navigateBack()
        }
    }

    BasicScaffold(
        chapterList.value?.title ?: "",
        isLoading = isLoading,
        fullScreen = true,
        enableActionBar = true,
        navController = navController,
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
                NeoTextField(
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
                            viewModel.editBook(updatedBook)
                        }
                    )
                }
                Text("Chapter title: ")
                NeoTextField(chapterTitle)

                LaunchedEffect(Unit) {
                    viewModel.channelCreateChapterFlow.collect {
                        when(it) {
                            is EditBookViewModel.EventEdit.OnCreateChapterSuccess -> {
                                navController.navigate(
                                    EditChapterScreen(it.chapterId, it.chapterTitle)
                                )
                            }

                            is EditBookViewModel.EventEdit.OnError -> {
//                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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
                            navController.navigate(
                                EditChapterScreen(
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
    if (toast.value) NeoToast(toastMessage.toString(), toast)
}