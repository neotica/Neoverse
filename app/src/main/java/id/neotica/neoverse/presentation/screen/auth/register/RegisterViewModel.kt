package id.neotica.neoverse.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepository
): ViewModel() {

    private var _loginCred: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val loginCred = _loginCred.stateIn(viewModelScope, SharingStarted.Lazily, ApiResult.Loading())

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginCred.emit(ApiResult.Loading())
        authRepo.login(email, password)
            .onStart { _loginCred.value = ApiResult.Loading() }
            .collect { _loginCred.value = it }
    }

    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn()
    }

    fun userId() { authRepo.userId() }

    fun logout() { authRepo.logout() }
}