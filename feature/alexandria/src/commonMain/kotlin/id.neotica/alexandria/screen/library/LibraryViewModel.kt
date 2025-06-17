package id.neotica.alexandria.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.alexandria.domain.BookRepository
import id.neotica.alexandria.domain.PermissionRepository
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.model.User
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val bookRepo: BookRepository,
    private val permissionRepository: PermissionRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {
    private var _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books = _books.asStateFlow()

    private var _userFlow = MutableSharedFlow<User>()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading get() = _loading

    private var _isNotLogged: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNotLogged: StateFlow<Boolean> = _isNotLogged

    fun userName() = getUserNameUseCase.invoke()

    init {
        loginState()
        getUserRoomInstance()
    }

    fun loginState() = viewModelScope.launch {
        if (authRepo.isLoggedIn()) {
            getBooks()
            _isNotLogged.value = false
        } else {
            _isNotLogged.value = true
        }
    }

    fun getBooks() = viewModelScope.launch {
        bookRepo.getBooks().collect {
            when(it) {
                is ApiResult.Loading -> {
                    _loading.value = true
                    _errorMessage.value = null
                }
                is ApiResult.Success -> {
                    it.data?.content?.let { _books.value = it }
                    _loading.value = false
                }
                is ApiResult.Error -> {
                    _errorMessage.value = it.errorMessage
                    _loading.value = false
                }
            }
        }
    }

    fun getPermission(unit: () -> Unit) = viewModelScope.launch {
        permissionRepository.getPermissionList().collect {
            when(it) {
                is ApiResult.Loading -> _loading.value = true
                is ApiResult.Success -> {
                    _loading.value = false
                    val result = it.data?.contains(userName()) == true
                    if (result) unit()
                }
                is ApiResult.Error -> {
                    _errorMessage.value = it.errorMessage
                    _loading.value = false
                }
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
        // TODO: make userDatabase schema in postgres
        val user = profileRepo.getUserDetail(authRepo.getUserId().toString())

        when(user) {
            is ApiResult.Loading -> _loading.value = true
            is ApiResult.Success -> {
                _loading.value = false
                user.data?.let {
                    _userFlow.emit(it)
                    currentUserDatabase.saveUser(it)
                }
            }
            is ApiResult.Error -> {
                when(user.errorMessage.toString()) {
                    "Invalid document reference. Document references must have an even number of segments, but Users has 1" -> {}
                    else -> {
                        _errorMessage.value = user.errorMessage
                    }
                }
                _loading.value = false
            }
        }
    }
}