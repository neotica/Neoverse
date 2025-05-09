package id.neotica.alexandria.screen.user.editbook

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.ui.navigation.AlexandriaScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBookViewModel(
    private val bookRepo: BookRepository,
    private val chapterRepo: ChapterRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private var _chapters: MutableStateFlow<ApiResult<Book>> = MutableStateFlow(ApiResult.Loading())
    val chapters = _chapters.asStateFlow()

    private val _chapterCreated = MutableSharedFlow<ApiResult<Chapter>>()
    val chapterCreated = _chapterCreated.asSharedFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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
            Log.d("spirit", it.toString())
            _chapters.value = it
        }
    }

    fun createChapter(chapterRequest: ChapterRequest) = viewModelScope.launch {
        _chapterCreated.emit(ApiResult.Loading())
        _isLoading.value = true
        chapterRepo.createChapter(chapterRequest).collect {
            _chapterCreated.emit(it)
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    getChapters()
                    _isLoading.value = false
                    _message.emit("Created chapter.")
                }
                else -> {
                    _isLoading.value = false
                    _message.emit(it.errorMessage.toString())
                }
            }
        }
    }
}