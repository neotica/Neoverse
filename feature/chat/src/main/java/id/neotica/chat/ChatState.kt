package id.neotica.chat

import id.neotica.domain.model.chat.ChatMessage

data class ChatState(
    val peerUsername: String = "",
    val currentUserId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isSending: Boolean = false,
    val toastMessage: String? = null,
)

sealed interface ChatEvent {
    data object ConversationDeleted : ChatEvent
}
