package id.neotica.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    private val conversationRepo: ConversationRepository,
    private val session: SessionRepository,
) : ViewModel() {

    val username: String = session.getUsername().orEmpty()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun isLoggedIn(): Boolean = authRepo.isLoggedIn()

    fun logout() = viewModelScope.launch {
        authRepo.logout()
    }

    fun deleteAccount() = viewModelScope.launch {
        conversationRepo.deleteMyChatData()
        when (val result = authRepo.deleteAccount()) {
            is ApiResult.Success -> _events.send(ProfileEvent.AccountDeleted)
            is ApiResult.Error -> _events.send(ProfileEvent.Error(result.errorMessage ?: "Unable to delete account"))
            is ApiResult.Loading -> Unit
        }
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        when (val result = authRepo.requestPasswordReset(email)) {
            is ApiResult.Success -> _events.send(ProfileEvent.Message(result.data ?: "Reset email sent"))
            is ApiResult.Error -> _events.send(ProfileEvent.Error(result.errorMessage ?: "Unable to send reset email"))
            is ApiResult.Loading -> Unit
        }
    }


    fun changeProfilePic(uri: String) = Unit

    fun onPermissionResult(permission: String, isGranted: Boolean) = Unit

    sealed interface ProfileEvent {
        data object AccountDeleted : ProfileEvent
        data class Message(val text: String) : ProfileEvent
        data class Error(val text: String) : ProfileEvent
    }
}
