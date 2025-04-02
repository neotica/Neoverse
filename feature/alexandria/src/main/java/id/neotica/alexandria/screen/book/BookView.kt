package id.neotica.alexandria.screen.book

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterContent
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NeoPadding
import org.koin.androidx.compose.koinViewModel

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

    LaunchedEffect(showChapterSheet) {
        viewModel.getChapterDetail(chapter.id)
        viewModel.chapterDetail.collect { result ->
            when (result) {
                is ApiResult.Loading -> isLoadingChapter = true
                is ApiResult.Success -> {
                    isLoadingChapter = false
                    chapterDetail = result.data
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
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        chapterDetail.let {
                            if (it == null) {
                                Text(chapterDetailStat)
                            } else {
                                Text(
                                    text = chapter.title,
                                    fontSize = FontSize.Medium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                chapterDetail?.let { detail ->
                                    Log.d("contenttt", detail.toString())
                                    ChapterContent(
                                        title = chapter.title,
                                        content = detail.content,
//                                        imageList = detail.
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (detail.hasPrevious) NeoButton("<") {
                                            viewModel.getChapterDetail(chapter.id, page = detail.page - 1)
                                        }
                                        if (detail.hasNext) NeoButton(">") {
                                            Log.d("detailpage", detail.page.toString())
                                            viewModel.getChapterDetail(chapter.id, page = detail.page + 1)
                                        }
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Add margin between items
                                    ) {
                                        items(detail.totalPages) { pageNum ->
                                            Text(
                                                text = (pageNum + 1).toString(),
                                                fontWeight = if (pageNum + 1 == detail.page) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp) // Add padding per item
                                                    .clickable {
                                                        viewModel.getChapterDetail(chapter.id, page = pageNum + 1)
                                                    }
                                            )
                                        }
                                    }
                                }
                            }
                        }
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
        Spacer(modifier = Modifier.padding(top = NeoPadding.topBarPadding))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            NeoSearchBar(searchBar, label = "Search Chapters")
        }


        LazyColumn(
            contentPadding = PaddingValues(
                bottom = NeoPadding.systemBarPadding
            )
        ) {
            val spiritsFilter = chapterList.value?.chapters?.filter {
                it.title.contains(searchBar.value, ignoreCase = true)
            }
            spiritsFilter?.let { chap ->
                items(chap) { item ->
                    ChapterItem(
                        title = item.title,
                    ) {
                        showChapterSheet = true
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