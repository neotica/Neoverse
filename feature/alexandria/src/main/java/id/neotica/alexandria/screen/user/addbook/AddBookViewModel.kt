package id.neotica.alexandria.screen.user.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val alexandriaRepo: AlexandriaRepository,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _book = MutableSharedFlow<ApiResult<Book>>()
    val book = _book.asSharedFlow()

    fun username() = getUserNameUseCase.invoke()

    fun addBook(title: String) = viewModelScope.launch {
        _book.emit(ApiResult.Loading())
        val book = BookRequest(
            title = title,
            author = username()
        )
        alexandriaRepo.addBook(book).collect {
            _book.emit(it)
        }
    }
}