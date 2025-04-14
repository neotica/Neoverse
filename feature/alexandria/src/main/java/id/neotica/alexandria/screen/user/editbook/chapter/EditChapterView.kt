package id.neotica.alexandria.screen.user.editbook.chapter

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import id.neotica.domain.ApiResult
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.edittext.EditContentBox
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.navigateBack
import id.neotica.ui.page.PageDropdown
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterView(
    navController: NavController,
    viewModel: EditChapterViewModel = koinViewModel()
) {

    var isLoadingChapter by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var chapterDetail by remember { mutableStateOf<PaginatedContent?>(null) }
    var chapterDetailStat by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.editContent.collect {
            when (it) {
                is ApiResult.Loading -> isLoadingChapter = true
                is ApiResult.Success -> {
                    isLoadingChapter = false
                    Toast.makeText(context, it.data, Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Error -> isLoadingChapter = false
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.chapterDetail.collect { result ->
            when (result) {
                is ApiResult.Loading -> {
                    chapterDetail = null
                    isLoadingChapter = true
                }
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

    BasicScaffold(
        topBarTitle = viewModel.chapterTitle,
        enableActionBar = true,
        navController = navController,
        isLoading = isLoadingChapter,
        trailingIcon = {
            LaunchedEffect(Unit) {
                viewModel.chapterDeleted.collect {
                    when (it) {
                        is ApiResult.Loading -> isLoadingChapter = true
                        is ApiResult.Success -> {
                            isLoadingChapter = false
                            navController.navigateBack()
                            Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> isLoadingChapter = false
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.clickable {
                        if (chapterDetail!= null) {
                            isLoadingChapter = true
                            viewModel.editContent(
                                content = contentText,
                                id = chapterDetail!!.id,
                                page = chapterDetail!!.page
                            )
                        } else {
                            Toast.makeText(context, "Chapter is null!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    text = "SAVE"
                )
                DotsMenuDropDown(
                    item = listOf(
                        DotsMenuItem("Delete Chapter") {
                            chapterDetail?.let { viewModel.deleteChapter(it.chapterId) }
                        }
                    ),
                )
            }
        }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .imePadding()
        ) {

//            if (isLoadingChapter) { LoadingIndicator() }

            chapterDetail.let {
                LaunchedEffect(Unit) {
                    viewModel.contentCreated.collect {
                        when (it) {
                            is ApiResult.Loading -> isLoadingChapter = true
                            is ApiResult.Success -> {
                                isLoadingChapter = false
                                navController.navigateBack()
                                Toast.makeText(context, "Content successfully created.", Toast.LENGTH_SHORT).show()
                            }
                            is ApiResult.Error -> {
                                Toast.makeText(context, it.errorMessage.toString(), Toast.LENGTH_SHORT).show()
                                isLoadingChapter = false
                            }
                        }
                    }
                }

                if (it == null) {
                    Column {
                        EditContentBox(
                            contentText = contentText,
                            onContentChange = { contentText = it }
                        )
                        NeoButton("Create") { viewModel.createContent(contentText) }
                    }
                } else {
                    chapterDetail?.let { detail ->

                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = 50.dp
                            )
                        ) {
                            item {
                                EditContentBox(
                                    contentText = contentText,
                                    onContentChange = { contentText = it }
                                )
                            }
                        }
                        LaunchedEffect(detail.content) {
                            contentText = detail.content
                        }
                    }
                }
            }
            Row(
                modifier= Modifier
                    .align(Alignment.BottomCenter)
                ,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                chapterDetail?.let { detail ->
                    if (detail.hasPrevious) NeoButton("<") {
                        viewModel.getChapterDetail(page = detail.page - 1)
                    }
                    if (detail.hasNext) NeoButton(">") {
                        Log.d("detailpage", detail.page.toString())
                        viewModel.getChapterDetail(page = detail.page + 1)
                    }
                    val pageCount = detail.totalPages

                    PageDropdown(
                        currentValue = detail.page,
                        count = pageCount
                    ) { viewModel.getChapterDetail(page = it.toInt() + 1) }
                }
            }
        }
    }
}