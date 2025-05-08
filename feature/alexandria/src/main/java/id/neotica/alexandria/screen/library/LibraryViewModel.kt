package id.neotica.alexandria.screen.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.model.User
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val bookRepo: BookRepository,
    private val permissionRepository: PermissionRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val userRepo: UserRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {
    private var _booksOld: MutableStateFlow<ApiResult<Pagination<Book>>> = MutableStateFlow(
        ApiResult.Loading())
    val books = _booksOld.asStateFlow()

    private var _permission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val permission = _permission.asStateFlow()

    private var _userFlow = MutableSharedFlow<User>()
    val userFlow = _userFlow.asSharedFlow()

    private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading get() = _loading

    fun userName() = getUserNameUseCase.invoke()

    init {
        getBooks()
        getPermission()
        getUserRoomInstance()
    }

    private fun getBooks() = viewModelScope.launch {
        bookRepo.getBooks().collect {
            when(it) {
                is ApiResult.Loading -> _loading.value = true
                is ApiResult.Success -> {
                    _booksOld.value = it
                    _loading.value = false
                }
                is ApiResult.Error -> _loading.value = false
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun getPermission() = viewModelScope.launch {
        permissionRepository.getPermissionList().collect {
            when(it) {
                is ApiResult.Loading -> _loading.value = true
                is ApiResult.Success -> {
                    _loading.value = false
                    _permission.value = it.data?.contains(userName()) == true
                    Log.d("permissionList", it.data .toString())
                }
                is ApiResult.Error -> _loading.value = false
            }
        }
    }

    private fun getUserRoomInstance() = viewModelScope.launch {
        currentUserDatabase.getUser().collect {
            if (it.isNotEmpty()) {
                _userFlow.emit(it[0])
            } else {
                getUser()
            }
        }
    }

    private fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())

        when(user) {
            is ApiResult.Loading -> _loading.value = true
            is ApiResult.Success -> {
                _loading.value = false
                user.data?.let {
                    _userFlow.emit(it)
                    currentUserDatabase.saveUser(it)
                }
            }
            is ApiResult.Error -> _loading.value = false
        }
    }

    suspend fun hasAuthorPermission(): Boolean {
        return permissionRepository.hasPermission(userName())
    }
}