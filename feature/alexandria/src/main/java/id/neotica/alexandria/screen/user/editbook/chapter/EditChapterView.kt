package id.neotica.alexandria.screen.user.editbook.chapter

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
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
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.edittext.EditContentBox
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.navigation.navigateBack
import id.neotica.ui.navigation.refreshOnBack
import id.neotica.ui.page.PageDropdown
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterView(
    navController: NavController,
    viewModel: EditChapterViewModel = koinViewModel()
) {

    val isLoadingChapter by viewModel.isLoading.collectAsState()

    val context = LocalContext.current

    val chapterDetail by viewModel.chapterDetail.collectAsState()
    var contentText by remember { mutableStateOf("") }
    val message by viewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect{
            navController.refreshOnBack()
            navController.navigateBack()
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
                        is ApiResult.Loading -> {}
                        is ApiResult.Success -> {
                            navController.refreshOnBack()
                            navController.navigateBack()
                            Toast.makeText(context, it.data.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is ApiResult.Error -> {}
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
                            viewModel.editContent(
                                content = contentText,
                                id = chapterDetail!!.id,
                                page = chapterDetail!!.page
                            )
                        } else {
                            viewModel.createContent(contentText)
                        }
                    },
                    text = "SAVE"
                )
                DotsMenuDropDown(
                    item = listOf(
                        DotsMenuItem("Delete Chapter") {
                            viewModel.deleteChapter()
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

            chapterDetail.let {
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
                chapterDetail?.let { detail ->
                    LaunchedEffect(detail.content) {
                        contentText = detail.content
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