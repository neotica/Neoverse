package id.neotica.profile.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun resetPassword() {
        if (!_state.value.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, toastMessage = null) }
            when (val result = authRepo.requestPasswordReset(_state.value.email)) {
                is ApiResult.Success -> _state.update {
                    it.copy(isLoading = false, toastMessage = result.data ?: "Reset email sent")
                }
                is ApiResult.Error -> _state.update {
                    it.copy(isLoading = false, toastMessage = result.errorMessage ?: "Unable to send reset email")
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
