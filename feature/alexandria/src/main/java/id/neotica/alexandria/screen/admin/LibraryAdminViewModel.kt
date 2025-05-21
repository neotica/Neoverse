package id.neotica.alexandria.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.admin.WarningRepository
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryAdminViewModel(
    private val bookRepo: BookRepository,
    private val warningRepo: WarningRepository,
    private val permissionRepository: PermissionRepository,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private var _booksOld: MutableStateFlow<ApiResult<Pagination<Book>>> = MutableStateFlow(
        ApiResult.Loading())
    val books = _booksOld.asStateFlow()

    private var _permission = MutableSharedFlow<Boolean>()
    val permission = _permission.asSharedFlow()

    private var _isLoading = MutableSharedFlow<Boolean>()
    val isLoading = _isLoading.asSharedFlow()

    fun userName() = getUserNameUseCase.invoke()

    init {
        getPermission()
    }

    private fun getBooks() = viewModelScope.launch {
        _isLoading.emit(true)
        warningRepo.getAllBooks().collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.emit(true)
                is ApiResult.Success -> {
                    _booksOld.value = it
                    _isLoading.emit(false)
                }
                is ApiResult.Error -> _isLoading.emit(false)
            }

        }
    }

    private fun getPermission() = viewModelScope.launch {
        permissionRepository.getPermissionList().collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.emit(true)
                is ApiResult.Success -> {
                    _isLoading.emit(false)
                    val permissionValue = it.data?.contains(userName()) == true
                    _permission.emit(permissionValue)
                    if (permissionValue) {
                        getBooks()
                    }
                }
                is ApiResult.Error -> {
                    _isLoading.emit(false)
                    _permission.emit(false)
                }
            }
        }
    }

    fun publish(id: String, publish: BookRequest) = viewModelScope.launch {
        bookRepo.editBook(id, publish).collect {
            when (it) {
                is ApiResult.Loading -> _isLoading.emit(true)
                is ApiResult.Success -> _isLoading.emit(false)
                is ApiResult.Error -> _isLoading.emit(false)
            }
        }
    }
}