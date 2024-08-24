package id.neotica.neoverse.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RegisterViewModel(
    private val authRepo: AuthRepository
): ViewModel() {

    private var _loginCred: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val loginCred = _loginCred.stateIn(viewModelScope, SharingStarted.Lazily, ApiResult.Loading())

}