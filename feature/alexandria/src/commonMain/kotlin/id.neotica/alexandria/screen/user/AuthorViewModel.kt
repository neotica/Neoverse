package id.neotica.alexandria.screen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import id.neotica.alexandria.domain.BookRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthorViewModel(
    private val bookRepo: BookRepository,
    private val userNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _books: MutableStateFlow<Pagination<Book>?> = MutableStateFlow(null)
    val books = _books.asStateFlow()

    private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading get() = _loading

    private var _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private fun username()= userNameUseCase.invoke()

    init {
        getBooks()
    }

    fun getBooks() = viewModelScope.launch {
        bookRepo.getUserBooks(username()).collect {
            when (it) {
                is ApiResult.Loading -> {
                    _loading.value = true
                    _message.value = null
                }
                is ApiResult.Success -> {
                    _loading.value = false
                    _books.value = it.data!!
                }
                is ApiResult.Error -> {
                    _loading.value = false
                    Logger.e { "AuthorVM, ${it.errorMessage}" }
                    _message.value = it.errorMessage
                }
            }
        }
    }
}