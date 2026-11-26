package id.neotica.profile.presentation.deleteaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.ConversationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeleteAccountViewModel(
    private val authRepo: AuthRepository,
    private val conversationRepo: ConversationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DeleteAccountState())
    val state = _state.asStateFlow()

    private val _events = Channel<DeleteAccountEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onConfirmationChange(value: String) {
        _state.update { it.copy(confirmation = value) }
    }

    fun deleteAccount() {
        if (!_state.value.canConfirm) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, toastMessage = null) }
            conversationRepo.deleteMyChatData()
            when (val result = authRepo.deleteAccount()) {
                is ApiResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(DeleteAccountEvent.AccountDeleted)
                }
                is ApiResult.Error -> _state.update {
                    it.copy(isLoading = false, toastMessage = result.errorMessage ?: "Unable to delete account")
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
