package id.neotica.alexandria.screen.library

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.model.User
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val alexandriaRepo: AlexandriaRepository,
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

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private var _currentUser: MutableStateFlow<ApiResult<User>?> = MutableStateFlow(
        ApiResult.Loading())
    val currentUser = _currentUser.asStateFlow()

    var current by mutableStateOf<User?>(null)

    fun userName() = getUserNameUseCase.invoke()

    init {
        getBooks()
        getPermission()
        getUserRoomInstance()
    }

    private fun getBooks() = viewModelScope.launch {
        alexandriaRepo.getBooks().collect {
            _booksOld.value = it
        }
    }

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun getPermission() = viewModelScope.launch {
        permissionRepository.getPermissionList().collect {
            if (it is ApiResult.Success) {
                _permission.value = it.data?.contains(userName()) == true
                Log.d("permissionList", it.toString())
            }
        }
    }

    private fun getUserRoomInstance() = viewModelScope.launch {
        currentUserDatabase.getUser().collect {
            if (it.isNotEmpty()) {
                current = it[0]
                _currentUser.value = ApiResult.Success(it[0])
            } else {
                getUser()
            }
        }
    }

    private fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
        user.data?.let { currentUserDatabase.saveUser(it) }
    }

    suspend fun hasAuthorPermission(): Boolean {
        return permissionRepository.hasPermission(userName())
    }
}