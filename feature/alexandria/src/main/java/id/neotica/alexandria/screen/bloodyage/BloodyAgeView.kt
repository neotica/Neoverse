package id.neotica.alexandria.screen.bloodyage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterContent
import id.neotica.alexandria.components.ChapterItem
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.BloodyAgeChapters
import id.neotica.domain.model.alexandria.ChapterDetail
import id.neotica.domain.model.alexandria.ChapterItem
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.droidcore.NeoSearchBar
import id.neotica.ui.theme.DarkBackground
import id.neotica.ui.theme.NeoPadding
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodyAgeView(
    navController: NavController,
    viewModel: BloodyAgeViewModel = koinViewModel()
) {
    val chapterList = remember { mutableStateOf<BloodyAgeChapters?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChapter by remember { mutableStateOf(false) }


    val searchBar = remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()
    var showChapterSheet by remember { mutableStateOf(false) }
    var chapter by remember {
        mutableStateOf(
            ChapterItem(
            id = 0,
            chapter = "",
            url = "",
            )
        )
    }
    var chapterDetail by remember { mutableStateOf<List<ChapterDetail>?>(null) }
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
        viewModel.getChapterDetail(chapter.url)
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
                chapterDetail = null
                chapter = ChapterItem(0,"", "")
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
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        chapterDetail.let {
                            if (it == null) {
                                Text(chapterDetailStat)
                            } else {
                                chapterDetail?.forEach { detail ->
                                    ChapterContent(
                                        title = chapter.chapter,
                                        content = detail.content,
                                        imageList = detail.images
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    BasicScaffold(
        "Bloody Age",
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
            val spiritsFilter = chapterList.value?.chapter?.filter {
                it.chapter.contains(searchBar.value, ignoreCase = true)
            }
            spiritsFilter?.let {
                items(it) {
                    ChapterItem(
                        title = it.chapter,
                    ) {
                        showChapterSheet = true
                        chapter = ChapterItem(
                            it.id,
                            it.chapter,
                            it.url,
                        )
                    }
                }
            }

        }
    }
}