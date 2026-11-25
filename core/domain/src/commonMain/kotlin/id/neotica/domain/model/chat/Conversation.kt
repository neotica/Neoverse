package id.neotica.domain.model.chat

data class Conversation(
    val id: String = "",
    val peerId: String = "",
    val peerUsername: String = "",
    val peerAvatarUrl: String? = null,
    val lastMessage: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
