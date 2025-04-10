package id.neotica.alexandria.screen.user.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddBookViewModel(
    private val alexandriaRepo: AlexandriaRepository,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _book: MutableStateFlow<ApiResult<Book>> = MutableStateFlow(ApiResult.Loading())
    val book = _book.asStateFlow()

    fun username() = getUserNameUseCase.invoke()

    fun addBook(title: String) = viewModelScope.launch {
        val book = BookRequest(
            title = title,
            author = username()
        )
        alexandriaRepo.addBook(book).collect { _book.value = it }
    }
}