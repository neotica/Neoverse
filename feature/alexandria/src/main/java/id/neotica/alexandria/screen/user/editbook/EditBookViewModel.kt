package id.neotica.alexandria.screen.user.editbook

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBookViewModel(
    private val alexandriaRepo: AlexandriaRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private var _chapters: MutableStateFlow<ApiResult<Book>> = MutableStateFlow(ApiResult.Loading())
    val chapters = _chapters.asStateFlow()

    private var _chapterDetail: MutableStateFlow<ApiResult<PaginatedContent>> = MutableStateFlow(ApiResult.Loading())
    val chapterDetail = _chapterDetail.asStateFlow()

    private var _editContent: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val editContent = _editContent.asStateFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.BookScreen>()

    init {
        getChapters()
    }

    fun editBook(book: Book) = viewModelScope.launch {
        alexandriaRepo.editBook(book).collect {
            Log.d("spirit", it.toString())
            _chapters.value = it
        }
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

    fun createChapter(chapterRequest: ChapterRequest) = viewModelScope.launch {
        alexandriaRepo.createChapter(chapterRequest).collect { Log.d("chapter req", it.toString()) }
    }

    fun deleteChapter(chapterId: Int) = viewModelScope.launch {
        alexandriaRepo.deleteChapter(chapterId).collect { Log.d("chapter req", it.toString()) }
    }

    fun createContent(contentRequest: ContentRequest) = viewModelScope.launch {
        alexandriaRepo.createContent(contentRequest).collect { Log.d("chapter req", it.toString()) }
    }

    fun editContent(content: String, id: Int, page: Int) = viewModelScope.launch {
        alexandriaRepo.editContent(
            content = NewContentRequest(newContent = content),
            id = id,
            page = page
        ).collect {
            Log.d("edit content req", it.toString())
            _editContent.value = it
        }
    }

    fun deleteContent(contentId: Int) = viewModelScope.launch {
        alexandriaRepo.deleteContent(contentId).collect { Log.d("chapter req", it.toString()) }
    }
}