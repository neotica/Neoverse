package id.neotica.home

import id.neotica.domain.model.chat.Conversation

data class HomeState(
    val username: String = "",
    val query: String = "",
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) {
    val filteredConversations: List<Conversation>
        get() = if (query.isBlank()) {
            conversations
        } else {
            conversations.filter { conversation ->
                conversation.peerUsername.contains(query, ignoreCase = true) ||
                    conversation.lastMessage.contains(query, ignoreCase = true)
            }
        }
}

sealed interface HomeEvent {
    data object RequireLogin : HomeEvent
}
