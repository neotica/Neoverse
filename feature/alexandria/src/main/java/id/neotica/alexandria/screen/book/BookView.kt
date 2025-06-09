package id.neotica.alexandria.screen.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.theme.DarkBackground
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookView(
    navController: NavController,
    viewModel: BookViewModel = koinViewModel()
) {
    val chapterList = remember { mutableStateOf<Book?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChapter by remember { mutableStateOf(false) }


    val searchBar = remember { mutableStateOf("") }

    val contentChunks = remember { mutableStateListOf<String>() }

    val sheetState = rememberModalBottomSheetState()
    var showChapterSheet by remember { mutableStateOf(false) }
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

    LaunchedEffect(Unit) {
        viewModel.getChapterDetail(chapter.id)
        viewModel.chapterDetail.collect { result ->
            when (result) {
                is ApiResult.Loading -> isLoadingChapter = true
                is ApiResult.Success -> {
                    isLoadingChapter = false
                    chapterDetail = result.data
                    contentChunks.add(result.data!!.content)
                }
                is ApiResult.Error -> {
                    isLoadingChapter = false
                    chapterDetailStat = result.errorMessage.toString()
                }
            }
        }
    }


    if (showChapterSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showChapterSheet = false
                chapter = Chapter(
                    id = 0,
                    title = "",
                    bookId = "",
                    createdAt = 0,
                    modifiedAt = 0,
                )
            },
            sheetState = sheetState,
            containerColor = DarkBackground
        ) {
            if (isLoadingChapter) {
                Box(Modifier.fillMaxSize()) { LoadingIndicator() }
            } else {
                val listState = rememberLazyListState()

                LaunchedEffect(listState) {
                    chapterDetail?.let {
                        snapshotFlow {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            val totalItems = listState.layoutInfo.totalItemsCount
                            lastVisible == totalItems - 1
                        }.collect { reachedBottom ->
                            if (reachedBottom) {
                                if (it.hasNext == true) {
                                    viewModel.getChapterDetail(it.page + 1)
                                }
                            }
                        }
                    }
                }

                LazyColumn(
                    state= listState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(contentChunks.size) { index ->
                        Text(contentChunks[index])
                    }
                }
            }
        }
    }

    BasicScaffold(
        chapterList.value?.title ?: "",
        fullScreen = true
    ) {
        if (isLoading) LoadingIndicator()
        LazyColumn(
            contentPadding = it
        ) {
            val spiritsFilter = chapterList.value?.chapters?.filter {
                it.title.contains(searchBar.value, ignoreCase = true)
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    NeoSearchBar(searchBar, label = "Search Chapters")
                }
            }
            spiritsFilter?.let { chap ->
                items(chap) { item ->
                    ChapterItem(
                        title = item.title,
                    ) {
                        navController.navigate(AlexandriaScreen.BookContentScreen(item.id, item.title))
                    }
                }
            }
        }
    }
}