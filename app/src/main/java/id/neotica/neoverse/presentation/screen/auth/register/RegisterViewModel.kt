package id.neotica.neoverse.presentation.screen.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepository
): ViewModel() {

    private var _registerStatus: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val registerStatus = _registerStatus.stateIn(viewModelScope, SharingStarted.Lazily, ApiResult.Loading())

    fun register(username: String, email: String, password: String) = viewModelScope.launch {
        _registerStatus.emit(ApiResult.Loading())
        _registerStatus.value = authRepo.register(username, email, password)
    }
}