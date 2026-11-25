package id.neotica.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import id.neotica.routes.Screen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val conversationRepo: ConversationRepository,
    private val session: SessionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Screen.Conversation>()
    val conversationId: String = args.conversationId
    val peerUsername: String = args.peerUsername
    val messages = conversationRepo.getMessages(args.conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun currentUserId(): String = session.getUserId().orEmpty()

    fun sendMessage(text: String) = viewModelScope.launch {
        val body = text.trim()
        if (body.isEmpty()) return@launch
        conversationRepo.sendMessage(
            conversationId = conversationId,
            clientMessageId = UUID.randomUUID().toString(),
            body = body,
        )
    }

    fun deleteConversation() = viewModelScope.launch {
        conversationRepo.deleteConversation(conversationId)
    }
}
