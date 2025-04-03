package id.neotica.alexandria.screen.user.editbook.chapter

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.alexandria.components.ChapterContent
import id.neotica.domain.ApiResult
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.DropdownItem
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.FontSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChapterView(
    navController: NavController,
    viewModel: EditChapterViewModel = koinViewModel()
) {

    var isLoading by remember { mutableStateOf(false) }
    var isLoadingChapter by remember { mutableStateOf(false) }


    val searchBar = remember { mutableStateOf("") }

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var showChapterSheet by remember { mutableStateOf(false) }
//    var chapter by remember {
//        mutableStateOf(
//            Chapter(
//                id = 0,
//                title = "",
//                bookId = "",
//                createdAt = 0,
//                modifiedAt = 0,
//            )
//        )
//    }
    var chapterDetail by remember { mutableStateOf<PaginatedContent?>(null) }
    var chapterDetailStat by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }


//    LaunchedEffect(chapter.id) {
//        Log.d("EditBookView", "Fetching chapter details for id: ${chapter.id}")
//    }

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


    BasicScaffold("Content") {
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
                            val contentText = remember { mutableStateOf("") }
                            Text("Content: ")
                            NeoTextField(contentText)
                            NeoButton("Create") { viewModel.createContent(contentText.value) }
                            Text(chapterDetailStat)
                        } else {
                            Text(
                                text = "chapterDetail.title",
                                fontSize = FontSize.Medium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            chapterDetail?.let { detail ->


                                var editState by remember { mutableStateOf(false) }

                                if (!editState) {
                                    ChapterContent(
                                        "title = cha.title",
                                        content = detail.content,
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = contentText,
                                        onValueChange = { textt ->
                                            contentText = textt
                                        },
                                        modifier = Modifier.height(500.dp)
                                    )
                                }
                                NeoButton("Delete content") { viewModel.deleteContent(detail.id) }
                                LaunchedEffect(detail.content) {
                                    contentText = detail.content
                                }

                                NeoButton("Edit") {
                                    editState = !editState
                                }



                                NeoButton("Update content") {
                                    isLoadingChapter = true
                                    viewModel.editContent(
                                        content = contentText,
                                        id = detail.id,
                                        page = detail.page
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    chapterDetail?.let {
                                        if (detail.hasPrevious) NeoButton("<") {
                                            viewModel.getChapterDetail(page = detail.page - 1)
                                        }
                                        if (detail.hasNext) NeoButton(">") {
                                            Log.d("detailpage", detail.page.toString())
                                            viewModel.getChapterDetail(page = detail.page + 1)
                                        }
                                    }

                                    val pageCount = detail.totalPages
                                    var dropDown by remember { mutableStateOf(false) }
                                    var dropDownValue by remember { mutableIntStateOf(0) }

                                    Card() {
                                        Row(
                                            modifier= Modifier
                                                .clickable { dropDown = true }
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text("${dropDownValue + 1}")
                                            Icon(painterResource(
                                                R.drawable.ic_arrow_drop_down),
                                                contentDescription = "Dropdown to select page"
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = dropDown,
                                            onDismissRequest = {
                                                dropDown = false
                                            }
                                        ) {
                                            repeat(pageCount) {
                                                DropdownItem("${it + 1}") {
                                                    dropDown = false
                                                    viewModel.getChapterDetail(page = it.toInt() + 1)
                                                    dropDownValue = it
                                                }
                                            }
                                        }
                                    }


                                }

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Add margin between items
                                ) {
                                    items(detail.totalPages) { pageNum ->
                                        Text(
                                            text = (pageNum + 1).toString(),
                                            fontWeight = if (pageNum + 1 == detail.page) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp, vertical = 8.dp) // Add padding per item
                                                .clickable {
                                                    viewModel.getChapterDetail(
                                                        page = pageNum + 1
                                                    )
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