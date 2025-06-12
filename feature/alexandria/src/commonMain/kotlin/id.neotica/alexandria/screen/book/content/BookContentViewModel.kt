package id.neotica.alexandria.screen.book.content

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.alexandria.domain.ContentRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.routes.AlexandriaScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookContentViewModel(
    private val contentRepo: ContentRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val args = savedStateHandle.toRoute<AlexandriaScreen.BookContentScreen>()

    val chapterTitle = args.chapterTitle

    private var _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private var loading by mutableStateOf(false)
    var hasNext by mutableStateOf(false)
    var paginatedContent by mutableStateOf<Map<Int, PaginatedContent>>(emptyMap())

    var totalPage by mutableIntStateOf(0)
        private set

    init {
        getPage()
    }

    fun setCurrentPage(page: Int) {
        _currentPage.value = page
    }

    fun getPage(refresh: Boolean = true) = viewModelScope.launch {
        if (loading) return@launch
        if (refresh) {
            loading = true
            _currentPage.value = 1
            paginatedContent = emptyMap()
        }
        if (hasNext || refresh) {
            loadPage(if (refresh) 1 else _currentPage.value + 1)
        }
    }

    fun loadPage(page: Int) = viewModelScope.launch {
        loading = true
        contentRepo.getContents(args.chapterId, page).collect {
            when (it) {
                is ApiResult.Success -> {
                    paginatedContent = paginatedContent + (page to it.data!!)
                    hasNext = it.data!!.hasNext
                    _currentPage.value = page
                    loading = false
                    totalPage = it.data!!.totalPages
                }
                is ApiResult.Error -> { loading = false }
                is ApiResult.Loading -> { loading = true }
            }
        }
    }

    suspend fun ensurePagesUpTo(targetPage: Int) {
        for (page in 1..targetPage) {
            if (!paginatedContent.containsKey(page)) {
                loadPage(page)
                while (!paginatedContent.containsKey(page)) {
                    delay(100)
                }
            }
        }
    }

}