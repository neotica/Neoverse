package id.neotica.alexandria.screen.book.content

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicator
import id.neotica.ui.components.NeoButton
import id.neotica.ui.theme.NeoPadding
import kotlinx.coroutines.coroutineScope
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
    var isLoadingChapter by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var chapterDetail by remember { mutableStateOf<PaginatedContent?>(null) }
    val scope = rememberCoroutineScope()
    var currentPage by remember { mutableIntStateOf(1) } // Initial page

    val contentChunks = remember { mutableStateListOf<String>() }
    val pageItemIndexMap = remember { mutableMapOf<Int, Int>() }
    val indexPageMap = remember { mutableMapOf<Int, Int>() }

    val listState = rememberLazyListState()
    var isPageJumpLoading by remember { mutableStateOf(false) }

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

                    val page = result.data!!.page
                    val content = result.data!!.content

                    // Check if this page's content is already added to avoid duplicates
                    if (!pageItemIndexMap.containsKey(page)) {
                        val currentIndex = contentChunks.size
                        contentChunks.add(content)
                        pageItemIndexMap[page] = currentIndex
                        indexPageMap[currentIndex] = page // Maintain reverse map
                        Log.d("BookContentView", "Page $page added at index $currentIndex")
                    } else {
                        // Optional: Update content if it can change?
                        // val existingIndex = pageItemIndexMap[page]!!
                        // contentChunks[existingIndex] = content
                        Log.d(
                            "BookContentView",
                            "Page $page already exists at index ${pageItemIndexMap[page]}"
                        )
                    }
                }

                is ApiResult.Error -> {
                    isLoadingChapter = false
                }
            }
        }
    }

    LaunchedEffect(listState, indexPageMap.size) { // Rerun if listState changes or map updates
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { firstVisibleIndex ->
                // Find the page number corresponding to the first visible item index
                // Iterate backwards from the visible index to find the closest starting index
                var pageForVisibleIndex = 1 // Default
                for (index in firstVisibleIndex downTo 0) {
                    if (indexPageMap.containsKey(index)) {
                        pageForVisibleIndex = indexPageMap[index]!!
                        break // Found the page that this visible item belongs to
                    }
                }
                Log.d(
                    "ScrollListener",
                    "FirstVisibleIndex: $firstVisibleIndex -> Mapped Page: $pageForVisibleIndex"
                )
                pageForVisibleIndex
            }
            .distinctUntilChanged() // Only emit when the calculated page actually changes
            .collectLatest { page -> // Use collectLatest to cancel previous computations if index changes fast
                if (currentPage != page) {
                    Log.d("ScrollListener", "Updating currentPage state to: $page")
                    currentPage = page // Update the state
                }
            }
    }

    suspend fun scrollToPage(
        targetPage: Int,
        listState: LazyListState,
        viewModel: BookContentViewModel,
        pageItemIndexMap: Map<Int, Int>,
        contentChunksSize: Int, // Pass current size for logging/fallback
        setPageJumpLoading: (Boolean) -> Unit, // Callback to show/hide loading indicator
        context: android.content.Context // Pass context for potential Toasts
    ) {
        // 1. Check if the target page is already loaded and its index known
        if (pageItemIndexMap.containsKey(targetPage)) {
            pageItemIndexMap[targetPage]?.let { index ->
                Log.d("ScrollToPage", "Page $targetPage already loaded at index $index. Scrolling.")
                listState.animateScrollToItem(index)
            }
            return // Already loaded, no need to fetch
        }

        Log.d("ScrollToPage", "Target page $targetPage not loaded. Initiating load.")
        setPageJumpLoading(true) // Show loading indicator specific to page jump

        try {
            // 2. Identify missing pages
            val highestLoadedPage = pageItemIndexMap.keys.maxOrNull() ?: 0
            // Ensure we only load pages greater than the highest *already* loaded
            // and up to the target page.
            val pagesToLoad = (highestLoadedPage + 1)..targetPage

            Log.d(
                "ScrollToPage",
                "Highest loaded: $highestLoadedPage. Pages to load: ${pagesToLoad.toList()}"
            )

            // 3. Fetch Missing Pages Concurrently
            if (!pagesToLoad.isEmpty()) {
                coroutineScope { // Create a scope to manage concurrent loads
                    pagesToLoad.forEach { page ->
                        // Double-check inside loop, map might update while iterating
                        if (!pageItemIndexMap.containsKey(page)) {
                            Log.d("ScrollToPage", "Launching load request for page $page")
                            // Launch network request, assuming getChapterDetail handles dispatcher switching
                            launch {
                                viewModel.getChapterDetail(page)
                            }
                        } else {
                            Log.d("ScrollToPage", "Page $page was already loaded concurrently.")
                        }
                    }
                } // coroutineScope waits for all launched jobs within it to *start*
                // Note: It doesn't wait for them to *complete* network/data processing
                // The LaunchedEffect collector handles the actual data addition
                Log.d("ScrollToPage", "All load requests launched for pages up to $targetPage.")
            } else {
                Log.d("ScrollToPage", "No new pages needed to load up to $targetPage.")
            }


            // 4. Wait for Target Page Data to appear in the map (with timeout)
            val maxWaitTimeMillis = 15000L // e.g., 15 seconds timeout
            val checkIntervalMillis = 100L
            var waitedTimeMillis = 0L

            while (!pageItemIndexMap.containsKey(targetPage) && waitedTimeMillis < maxWaitTimeMillis) {
                delay(checkIntervalMillis)
                waitedTimeMillis += checkIntervalMillis
                Log.d("ScrollToPage", "Waiting for page $targetPage data... ($waitedTimeMillis ms)")
            }

            // 5. Scroll (if target page loaded successfully within timeout)
            if (pageItemIndexMap.containsKey(targetPage)) {
                pageItemIndexMap[targetPage]?.let { index ->
                    Log.d("ScrollToPage", "Page $targetPage data found at index $index. Scrolling.")
                    listState.animateScrollToItem(index)
                } ?: run {
                    // This case should be rare if the key exists
                    Log.e(
                        "ScrollToPage",
                        "Error: Page $targetPage key exists in map, but value is null!"
                    )
                    // Fallback: Scroll towards the end maybe?
                    if (contentChunksSize > 0) listState.animateScrollToItem(contentChunksSize - 1)
                }
            } else {
                Log.e(
                    "ScrollToPage",
                    "Timeout or error: Page $targetPage data did not appear in map after ${maxWaitTimeMillis}ms."
                )
                Toast.makeText(context, "Failed to load page $targetPage", Toast.LENGTH_SHORT)
                    .show()
                // Optional: Scroll to the last loaded item as a fallback
                pageItemIndexMap.values.maxOrNull()?.let { lastKnownIndex ->
                    listState.animateScrollToItem(lastKnownIndex)
                }
            }

        } catch (e: Exception) {
            Log.e("ScrollToPage", "Exception during page loading/scrolling", e)
            Toast.makeText(context, "Error loading page", Toast.LENGTH_SHORT).show()
        } finally {
            setPageJumpLoading(false) // Hide loading indicator
        }
    }

    BasicScaffold(
        topBarTitle = viewModel.chapterTitle,
        fullScreen = true,
        enableActionBar = true,
        navController = navController,
    ) {
        Box(Modifier.fillMaxSize()) {

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
                // Display content chunks
                items(contentChunks.size, key = { index -> index }) { index ->
                    Text(contentChunks[index])
                }
                item {
                    val currentDetail = chapterDetail

                    LaunchedEffect(listState, currentDetail?.hasNext) {
                        snapshotFlow {
                            val layoutInfo = listState.layoutInfo
                            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                            val totalItems = layoutInfo.totalItemsCount

                            val nearEnd =
                                lastVisibleItem != null && totalItems > 0 && lastVisibleItem.index >= totalItems - 1
                            nearEnd
                        }
                            .distinctUntilChanged()
                            .collect { shouldLoadNext ->
                                if (shouldLoadNext && currentDetail?.hasNext == true && !isLoadingChapter) {
                                    val nextPage = currentDetail.page + 1
                                    Log.d("LazyColumn", "Reached near end, loading page $nextPage")
                                    isLoadingChapter = true // Show loading state while fetching
                                    viewModel.getChapterDetail(nextPage)
                                }
                            }
                    }
                    if (isLoadingChapter && chapterDetail?.hasNext == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator() // Smaller indicator
                        }
                    }
                }
            }
            chapterDetail?.let { detail ->
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = NeoPadding.systemBarPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    NeoButton(
                        text = "<",
                        enabled = currentPage > 1 // Enable based on dynamic currentPage
                    ) {
                        if (currentPage > 1) {
                            scope.launch {
                                scrollToPage(
                                    targetPage = currentPage - 1,
                                    listState = listState,
                                    viewModel = viewModel,
                                    pageItemIndexMap = pageItemIndexMap, // Pass the map
                                    contentChunksSize = contentChunks.size,
                                    setPageJumpLoading = { isPageJumpLoading = it }, // Pass setter
                                    context = context // Pass context
                                )
                            }
                        }
                    }

                    NeoButton(
                        text = ">",
                        enabled = currentPage < detail.totalPages // Enable based on dynamic currentPage and known totalPages
                    ) {
                        if (currentPage < detail.totalPages) {
                            scope.launch {
                                scrollToPage(
                                    targetPage = currentPage + 1,
                                    listState = listState,
                                    viewModel = viewModel,
                                    pageItemIndexMap = pageItemIndexMap, // Pass the map
                                    contentChunksSize = contentChunks.size,
                                    setPageJumpLoading = { isPageJumpLoading = it }, // Pass setter
                                    context = context // Pass context
                                )
                            }
                        }
                    }
                }
                if (detail.totalPages > 0) {
                    key(
                        currentPage,
                        detail.totalPages
                    ) { // Recompose if currentPage or totalPages changes
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = NeoPadding.systemBarPadding +4.dp, end = 16.dp),
                            text = currentPage.toString()
                        )
                    }
                } else {
                    Text("Page $currentPage", modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
            if (isPageJumpLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Add a semi-transparent background scrim
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f))
                        // Make it clickable to prevent interaction below
                        .clickable(enabled = false, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator() // Your loading indicator component
                }
            }
        }
    }
}