package id.neotica.alexandria.screen.book.content

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookContentViewModel(
    private val alexandriaRepo: AlexandriaRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var _chapterDetail: MutableStateFlow<ApiResult<PaginatedContent>> = MutableStateFlow(ApiResult.Loading())
    val chapterDetail = _chapterDetail.asStateFlow()


    private val args = savedStateHandle.toRoute<AlexandriaScreen.BookContentScreen>()

    val chapterTitle = args.chapterTitle

    init {
        getChapterDetail()
    }

    fun getChapterDetail(page: Int? = 1) = viewModelScope.launch {

        alexandriaRepo.getContents(args.chapterId, page).collect {
            Log.d("spirit", it.toString())
            _chapterDetail.value = it
        }
    }

}