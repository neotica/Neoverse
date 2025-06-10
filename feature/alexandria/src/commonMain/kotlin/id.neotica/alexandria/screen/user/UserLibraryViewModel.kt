package id.neotica.alexandria.screen.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserLibraryViewModel(
    private val bookRepo: BookRepository,
    private val userNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _booksOld: MutableStateFlow<ApiResult<List<Book>>> = MutableStateFlow(
        ApiResult.Loading())
    val books = _booksOld.asStateFlow()

    private fun username()= userNameUseCase.invoke()

    init {
        getBooks()
    }

    fun getBooks() = viewModelScope.launch {
        bookRepo.getUserBooks(username()).collect {
            _booksOld.value = it
//            Log.d("userlibrary", username())
        }
    }

}