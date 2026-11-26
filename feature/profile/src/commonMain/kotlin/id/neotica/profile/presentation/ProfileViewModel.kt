package id.neotica.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.session.SessionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    session: SessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileState(username = session.getUsername().orEmpty())
    )
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        if (!authRepo.isLoggedIn()) {
            viewModelScope.launch { _events.send(ProfileEvent.RequireLogin) }
        }
    }

    fun onAccountSettingsVisibilityChange(visible: Boolean) {
        _state.update { it.copy(showAccountSettings = visible) }
    }

    fun onLogoutDialogVisibilityChange(visible: Boolean) {
        _state.update { it.copy(showLogoutDialog = visible) }
    }

    fun showToast(message: String) {
        _state.update { it.copy(toastMessage = message) }
    }

    fun logout() = viewModelScope.launch {
        authRepo.logout()
        _events.send(ProfileEvent.LoggedOut)
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }

    fun changeProfilePic(uri: String) = Unit

    fun onPermissionResult(permission: String, isGranted: Boolean) = Unit
}
