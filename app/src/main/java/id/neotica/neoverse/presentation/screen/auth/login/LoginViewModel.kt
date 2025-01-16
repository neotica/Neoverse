package id.neotica.neoverse.presentation.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepo: AuthRepository,
): ViewModel() {

    private var _loginCred: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val loginCred = _loginCred.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginCred.emit(ApiResult.Loading())
        _loginCred.value = authRepo.login(email, password)
    }
}