package id.neotica.alexandria.screen.user.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val bookRepo: BookRepository,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _book = MutableSharedFlow<ApiResult<Book>>()
    val book = _book.asSharedFlow()

    private var _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private var _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun username() = getUserNameUseCase.invoke()

    fun addBook(title: String, language: String) = viewModelScope.launch {
        _isLoading.emit(true)
        val book = BookRequest(
            title = title,
            author = username(),
            language = language,
            isPublished = false
        )
        bookRepo.addBook(book).collect {
            when(it) {
                is ApiResult.Loading -> _isLoading.emit(true)
                is ApiResult.Success -> {
                    _isLoading.emit(false)
                    _toastMessage.emit("$title created!")
                }
                is ApiResult.Error -> {
                    _isLoading.emit(false)
                    _toastMessage.emit(it.errorMessage.toString())
                }
            }
            _book.emit(it)
        }
    }
}