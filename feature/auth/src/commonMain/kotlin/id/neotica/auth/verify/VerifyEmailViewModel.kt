package id.neotica.auth.verify

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

class VerifyEmailViewModel(
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyEmailState())
    val state = _state.asStateFlow()

    private val _events = Channel<VerifyEmailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun resend() {
        val email = _state.value.email
        if (email.isBlank() || _state.value.isResending) return
        viewModelScope.launch {
            _state.update { it.copy(isResending = true, toastMessage = null) }
            when (val result = authRepo.resendVerification(email)) {
                is ApiResult.Success -> _state.update {
                    it.copy(isResending = false, toastMessage = "Email sent.")
                }
                is ApiResult.Error -> _state.update {
                    it.copy(isResending = false, toastMessage = result.errorMessage ?: "Unable to send email")
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun logout() = viewModelScope.launch {
        authRepo.logout()
        _events.send(VerifyEmailEvent.LoggedOut)
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
