package id.neotica.alexandria.screen.user.editbook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditBookViewModel(
    private val bookRepo: BookRepository,
    private val chapterRepo: ChapterRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _book: MutableStateFlow<Book?> = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val channelCreateChapter = Channel<EventEdit>()
    val channelCreateChapterFlow = channelCreateChapter.receiveAsFlow()

    private var _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private var _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.EditBookScreen>()

    init {
        getChapters()
    }

    fun editBook(book: Book) = viewModelScope.launch {
        _isLoading.value = true
        val id = book.id
        val bookRequest = BookRequest(
            title = book.title,
            author = book.author,
            language = book.language,
            genre = book.genre,
            summary = book.summary,
            isPublished = book.isPublished,
            imageUrl = book.imageUrl
        )
        bookRepo.editBook(id, bookRequest).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _message.emit("Success.")
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.emit(it.errorMessage.toString())
                }
            }
        }
    }

    fun deleteBook() = viewModelScope.launch {
        _isLoading.value = true
        bookRepo.deleteBook(args.id).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _navigateBack.emit(true)
                    _message.emit("Success deleting book.")
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.emit(it.errorMessage.toString())
                }
            }
        }
    }

    fun getChapters() = viewModelScope.launch {
        bookRepo.getBook(args.id).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _book.value = it.data!!
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _message.emit(it.errorMessage.toString())
                }
            }
        }
    }

    fun createChapter(chapterRequest: ChapterRequest) = viewModelScope.launch {
        chapterRepo.createChapter(chapterRequest).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    getChapters()
                    _isLoading.value = false
                    channelCreateChapter.send(EventEdit.OnCreateChapterSuccess(it.data!!.id, it.data!!.title))
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    channelCreateChapter.send(EventEdit.OnError(it.errorMessage.toString()))
                }
            }
        }
    }

    sealed interface EventEdit {
        data class OnCreateChapterSuccess(val chapterId: Int = 0, val chapterTitle: String = ""): EventEdit
        data class OnError(val message: String): EventEdit
    }
}