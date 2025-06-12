package id.neotica.alexandria.screen.user.editbook.chapter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.alexandria.domain.ChapterRepository
import id.neotica.alexandria.domain.ContentRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.routes.AlexandriaScreen
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

    private var _chapterDeleted = MutableSharedFlow<ApiResult<String>>()
    val chapterDeleted = _chapterDeleted.asSharedFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _chapterDetail = MutableStateFlow<PaginatedContent?>(null)
    var chapterDetail = _chapterDetail.asStateFlow()

    private var _message = MutableStateFlow<String?>(null)
    val message = _message

    private var _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.EditChapterScreen>()

    val chapterTitle = args.chapterTitle

    init {
        getChapterDetail()
    }

    fun getChapterDetail(page: Int? = 1) = viewModelScope.launch {
        contentRepo.getContents(args.chapterId, page).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _chapterDetail.value = it.data
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    if (it.errorMessage == "404 Not Found") {
                        "You have not created content for this chapter."
                    } else {
                        _message.value = it.errorMessage
                    }

                }
            }
        }
    }

    fun deleteChapter() = viewModelScope.launch {
        chapterRepo.deleteChapter(args.chapterId).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _message.value = it.data
                    _navigateBack.emit(true)
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }

    fun createContent(content: String) = viewModelScope.launch {
        contentRepo.createContent(
            ContentRequest(
                content = content,
                chapterId = args.chapterId
            )
        ).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _message.value = "Successfully created content."
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }

    fun editContent(content: String, id: Int, page: Int) = viewModelScope.launch {
        contentRepo.editContent(
            content = NewContentRequest(newContent = content),
            id = id,
            page = page
        ).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    getChapterDetail(page)
                    _isLoading.value = false
                    _message.value = it.data
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.value = it.errorMessage
                }
            }
        }
    }
}