package id.neotica.alexandria.screen.book.content

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.topbar.DotsMenuDropDown
import id.neotica.ui.components.topbar.DotsMenuItem
import id.neotica.ui.page.PageDropdown
import id.neotica.ui.theme.DarkPrimaryTransparent2
import id.neotica.ui.theme.NeoPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookContentView(
    navController: NavController,
    viewModel: BookContentViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentPage by viewModel.currentPage.collectAsState()


    val listState = rememberLazyListState()

    var openPageSelector by remember { mutableStateOf(false) }

    val isMoreLoadingVisible by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo
                .filter { it.key.toString() == "loading" }
                .size == 1
        }
    }

    LaunchedEffect(key1 = isMoreLoadingVisible) {
        if (isMoreLoadingVisible) {
            Log.d("alexandria content", "Loading more content")
            viewModel.getPage(refresh = false)
        }
    }

    LaunchedEffect(listState, viewModel.paginatedContent.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val visiblePageItems = visibleItems.filter {
                    // Only include items whose key is an integer (i.e., actual page numbers)
                    it.key.toString().toIntOrNull() != null
                }

                val lastVisiblePageItem = visiblePageItems.maxByOrNull { it.offset }

                val page = lastVisiblePageItem?.key?.toString()?.toIntOrNull() ?: 1

                Log.d("ScrollListener", "FilteredVisible: $visiblePageItems -> Page: $page")
                page
            }
            .distinctUntilChanged()
            .collectLatest { page ->
                if (viewModel.currentPage.value != page) {
                    viewModel.setCurrentPage(page) // Create a setter if needed
                }
            }
    }

    BasicScaffold(
        topBarTitle = viewModel.chapterTitle,
        fullScreen = true,
        enableActionBar = true,
        trailingIcon = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotsMenuDropDown(
                    item = listOf(
                        DotsMenuItem(
                            "Goto Page: "
                        ) {
                            openPageSelector = true
                        }
                    )
                )
            }
        },
        navController = navController,
    ) {
        Box(Modifier.fillMaxSize()) {
            if (openPageSelector) {
                PageSelectorOverlay(
                    modifier = Modifier.align(Alignment.Center),
                    currentPage = currentPage,
                    totalPage = viewModel.totalPage,
                    onDismiss = { openPageSelector = false }
                ) { selectedPage ->
                    scope.launch {
                        viewModel.ensurePagesUpTo(selectedPage)

                        val sortedPages = viewModel.paginatedContent.toSortedMap().keys.toList()
                        val targetIndex = sortedPages.indexOf(selectedPage).coerceAtLeast(0)

                        listState.animateScrollToItem(targetIndex + 1)
                        viewModel.setCurrentPage(selectedPage + 1)
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    top = NeoPadding.topBarPadding,
                    bottom = NeoPadding.systemBarPadding + 50.dp
                )
            ) {
                items(
                    items = viewModel.paginatedContent.toSortedMap().values.toList(),
                    key = {it.page}
                ) {
                    Text(it.content)
                }
                if (viewModel.hasNext) {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            LoadingIndicator()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = NeoPadding.systemBarPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                NeoButton(
                    text = "<",
                    enabled = currentPage > 1
                ) {
                    val prevPage = currentPage - 1

                    scope.launch {
                        if (!viewModel.paginatedContent.containsKey(prevPage)) {
                            viewModel.loadPage(prevPage)
                        }
                        // Wait until the page is loaded
                        while (!viewModel.paginatedContent.containsKey(prevPage)) {
                            delay(100) // small delay to avoid tight loop
                        }

                        val updatedSortedKeys = viewModel.paginatedContent.toSortedMap().keys.toList()
                        val targetIndex = updatedSortedKeys.indexOf(prevPage).coerceAtLeast(0)

                        Log.d("ScrollToPrevPage", "Scrolling to index $targetIndex for page $prevPage")

                        listState.animateScrollToItem(targetIndex)
                    }
                }

                NeoButton(
                    text = ">",
                    enabled = viewModel.paginatedContent[currentPage]?.hasNext == true // Enable based on dynamic currentPage and known totalPages
                ) {
                    val nextPage = currentPage + 1
                    scope.launch {
                        if (!viewModel.paginatedContent.containsKey(nextPage)) {
                            viewModel.loadPage(nextPage)

                            // Wait until the next page is available in the map
                            while (!viewModel.paginatedContent.containsKey(nextPage)) {
                                delay(100)
                            }
                        }

                        // Now get the correct index after map is updated
                        val sortedPages = viewModel.paginatedContent.toSortedMap().keys.toList()
                        val targetIndex = sortedPages.indexOf(nextPage).coerceAtLeast(0)

                        Log.d("ScrollToNextPage", "Scrolling to index $targetIndex for page $nextPage")

                        listState.animateScrollToItem(targetIndex)
                    }
                }
                Text("Page $currentPage", modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
private fun PageSelectorOverlay(
    modifier: Modifier,
    currentPage: Int,
    totalPage: Int,
    onDismiss: () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    Card(
        modifier = modifier
            .zIndex(5f),
        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent2)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Go to page: ")
            if (totalPage > 0) {
                PageDropdown(
                    top = true,
                    currentValue = currentPage,
                    count = totalPage,
                    onValueChange = { selectedPage ->
                        onDismiss()
                        onPageSelected(selectedPage)
                    }
                )
            }
        }
    }
}