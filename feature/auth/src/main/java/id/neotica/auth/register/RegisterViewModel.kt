package id.neotica.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepository,
): ViewModel() {

    private var _registerStatus: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val registerStatus = _registerStatus.stateIn(viewModelScope, SharingStarted.Lazily, ApiResult.Loading())

    private var _isUserExist: MutableStateFlow<ApiResult<Boolean>> = MutableStateFlow(ApiResult.Loading())
    val isUserExist = _isUserExist.asStateFlow()

    fun register(username: String, email: String, password: String) = viewModelScope.launch {
        _registerStatus.emit(ApiResult.Loading())
        _registerStatus.value = authRepo.register(username, email, password)
    }

    fun checkUsername(username: String) = viewModelScope.launch {
        val user = authRepo.isUsernameExist(username)
        _isUserExist.value = user
    }
}