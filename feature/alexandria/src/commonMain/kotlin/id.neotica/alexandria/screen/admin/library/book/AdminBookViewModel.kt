package id.neotica.alexandria.screen.admin.library.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.alexandria.domain.BookRepository
import id.neotica.alexandria.domain.admin.WarningRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.admin.WarningRequest
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.routes.AlexandriaScreen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AdminBookViewModel(
    private val bookRepo: BookRepository,
    private val warningRepo: WarningRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _book: MutableStateFlow<Book?> = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private var _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack = _navigateBack.asSharedFlow()

    private val _channelCreateWarning = Channel<EventEdit>()
    val channelCreateWarning = _channelCreateWarning.receiveAsFlow()

    private val args = savedStateHandle.toRoute<AlexandriaScreen.AdminBookScreen>()

    init {
        getChapters()
    }

    fun createWarning(warningMessage: String) = viewModelScope.launch {
        val request = WarningRequest(
            bookId = args.id,
            message = warningMessage,
            version = 1,
            isApproved = false,
            isAppealed = false
        )
        warningRepo.newWarning(request).collect {
//            Log.d("createWarning", "createWarning all: ${it}")
            when (it) {
                is ApiResult.Loading -> _isLoading.value = true
                is ApiResult.Success -> {
                    _isLoading.value = false
                    val id = it.data?.id
                    _channelCreateWarning.send(EventEdit.OnCreateWarningSuccess(id.toString()))
//                    Log.d("createWarning", "createWarning: ${it.data}")
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _channelCreateWarning.send(EventEdit.OnError(it.errorMessage.toString()))
//                    _message.emit(it.errorMessage.toString())
//                    Log.d("createWarning", "createWarning error: ${it.errorMessage}")
                }
            }
        }
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

    sealed interface EventEdit {
        data class OnCreateWarningSuccess(val warningId: String): EventEdit
        data class OnError(val message: String): EventEdit
    }
}