package id.neotica.alexandria.screen.user.editbook.chapter

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.domain.remote.alexandria.ContentRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditChapterViewModel(
    private val chapterRepo: ChapterRepository,
    private val contentRepo: ContentRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private var _chapterDetail: MutableStateFlow<ApiResult<PaginatedContent>> = MutableStateFlow(ApiResult.Loading())
    val chapterDetail = _chapterDetail.asStateFlow()

    private var _editContent: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val editContent = _editContent.asStateFlow()

    private var _contentCreated = MutableSharedFlow<ApiResult<ChapterContent>>()
    val contentCreated = _contentCreated.asSharedFlow()

    private var _chapterDeleted = MutableSharedFlow<ApiResult<String>>()
    val chapterDeleted = _chapterDeleted.asSharedFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.EditChapterScreen>()

    val chapterTitle = args.chapterTitle

    init {
        getChapterDetail()
    }

    fun getChapterDetail(page: Int? = 1) = viewModelScope.launch {
        contentRepo.getContents(args.chapterId, page).collect {
            Log.d("chapterdetailvm", it.toString())
            _chapterDetail.value = it
        }
    }

    fun deleteChapter() = viewModelScope.launch {
        _chapterDeleted.emit(ApiResult.Loading())
        chapterRepo.deleteChapter(args.chapterId).collect {
            _chapterDeleted.emit(it)
        }
    }

    fun createContent(content: String) = viewModelScope.launch {
        _contentCreated.emit(ApiResult.Loading())
        contentRepo.createContent(
            ContentRequest(
                content = content,
                chapterId = args.chapterId
            )
        ).collect {
            _contentCreated.emit(it)
        }
    }

    fun editContent(content: String, id: Int, page: Int) = viewModelScope.launch {
        contentRepo.editContent(
            content = NewContentRequest(newContent = content),
            id = id,
            page = page
        ).collect {
            _editContent.value = it
            getChapterDetail(page)
        }
    }
}