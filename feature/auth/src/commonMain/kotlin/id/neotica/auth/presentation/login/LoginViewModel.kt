package id.neotica.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        if (!_state.value.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, toastMessage = null) }
            when (val result = authRepo.login(_state.value.username, _state.value.password)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.Success)
                }
                is ApiResult.Error -> {
                    val message = when (result.errorMessage) {
                        "EMAIL_NOT_VERIFIED" -> "Please verify your email before logging in."
                        else -> result.errorMessage.toString()
                    }
                    _state.update { it.copy(isLoading = false, toastMessage = message) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
