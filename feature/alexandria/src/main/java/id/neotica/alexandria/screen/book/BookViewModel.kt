package id.neotica.alexandria.screen.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(
    private val alexandriaRepo: AlexandriaRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private var _chapters: MutableStateFlow<ApiResult<Book>> = MutableStateFlow(ApiResult.Loading())
    val chapters = _chapters.asStateFlow()

    private var _chapterDetail: MutableStateFlow<ApiResult<PaginatedContent>> = MutableStateFlow(ApiResult.Loading())
    val chapterDetail = _chapterDetail.asStateFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.BookScreen>()

    init {
        getChapters()
    }

    private fun getChapters() = viewModelScope.launch {
        alexandriaRepo.getBook(args.url).collect {
            Log.d("spirit", it.toString())
            _chapters.value = it
        }
    }

    fun getChapterDetail(chapterId: Int, page: Int? = 1) = viewModelScope.launch {
        alexandriaRepo.getContents(chapterId, page).collect {
            Log.d("spirit", it.toString())
            _chapterDetail.value = it
        }
    }
}