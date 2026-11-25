package id.neotica.domain.model.chat

data class ChatMessage(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val body: String = "",
    val createdAt: Long = 0L,
    val clientMessageId: String? = null
)
