package id.neotica.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val authRepo: AuthRepository,
) : ViewModel() {

    fun resend(email: String) = viewModelScope.launch {
        authRepo.resendVerification(email)
    }

    fun logout() = viewModelScope.launch {
        authRepo.logout()
    }
}
