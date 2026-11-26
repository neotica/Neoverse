package id.neotica.home.addchat

import id.neotica.domain.model.chat.Conversation

data class AddChatState(
    val username: String = "",
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
) {
    val canSubmit: Boolean get() = username.isNotBlank() && !isLoading
}

sealed interface AddChatEvent {
    data class ConversationCreated(val conversation: Conversation) : AddChatEvent
}
