package id.neotica.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import id.neotica.routes.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val conversationRepo: ConversationRepository,
    private val session: SessionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Screen.Conversation>()
    private val conversationId = args.conversationId

    private val _state = MutableStateFlow(
        ChatState(
            peerUsername = args.peerUsername,
            currentUserId = session.getUserId().orEmpty(),
        )
    )
    val state = _state.asStateFlow()

    private val _events = Channel<ChatEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            conversationRepo.getMessages(conversationId).collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    fun onDraftChange(value: String) {
        _state.update { it.copy(draft = value) }
    }

    fun sendMessage() {
        val body = _state.value.draft.trim()
        if (body.isEmpty() || _state.value.isSending) return
        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            when (val result = conversationRepo.sendMessage(
                conversationId = conversationId,
                clientMessageId = UUID.randomUUID().toString(),
                body = body,
            )) {
                is ApiResult.Success -> _state.update {
                    it.copy(draft = "", isSending = false)
                }
                is ApiResult.Error -> _state.update {
                    it.copy(isSending = false, toastMessage = result.errorMessage ?: "Unable to send message")
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun deleteConversation() = viewModelScope.launch {
        when (val result = conversationRepo.deleteConversation(conversationId)) {
            is ApiResult.Success -> _events.send(ChatEvent.ConversationDeleted)
            is ApiResult.Error -> _state.update {
                it.copy(toastMessage = result.errorMessage ?: "Unable to delete conversation")
            }
            is ApiResult.Loading -> Unit
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }
}
