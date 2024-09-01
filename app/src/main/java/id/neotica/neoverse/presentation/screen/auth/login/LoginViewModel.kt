package id.neotica.neoverse.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.AuthRepository
import id.neotica.neoverse.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
): ViewModel() {

    private var _loginCred: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val loginCred = _loginCred.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginCred.emit(ApiResult.Loading())
        _loginCred.value = authRepo.login(email, password)
    }

    fun userId() { userRepo.getUserId() }
}