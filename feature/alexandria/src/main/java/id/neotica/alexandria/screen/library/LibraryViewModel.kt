package id.neotica.alexandria.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.AlexandriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val alexandriaRepo: AlexandriaRepository
): ViewModel() {
    private var _booksOld: MutableStateFlow<ApiResult<Pagination<Book>>> = MutableStateFlow(
        ApiResult.Loading())
    val books = _booksOld.asStateFlow()

    init {
        getBooks()
    }

    private fun getBooks() = viewModelScope.launch {
        alexandriaRepo.getBooks().collect {
            _booksOld.value = it
        }
    }
}