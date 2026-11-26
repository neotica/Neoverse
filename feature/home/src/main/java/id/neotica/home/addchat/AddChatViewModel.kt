package id.neotica.home.addchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.ConversationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddChatViewModel(
    private val conversationRepo: ConversationRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddChatState())
    val state = _state.asStateFlow()

    private val _events = Channel<AddChatEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value) }
    }

    fun startChat() {
        val target = _state.value.username.trim()
        if (target.isEmpty() || _state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, toastMessage = null) }
            when (val result = conversationRepo.getOrCreateConversation(target)) {
                is ApiResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    result.data?.let { _events.send(AddChatEvent.ConversationCreated(it)) }
                }
                is ApiResult.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        toastMessage = result.errorMessage ?: "Unable to start chat",
                    )
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
