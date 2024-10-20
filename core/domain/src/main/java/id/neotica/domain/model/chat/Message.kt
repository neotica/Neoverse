package id.neotica.domain.model.chat

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderImage: String? = null,
    val read: Boolean = false,
    val imageUrl: String? = null
)

data class ParentMessage(
    val key: String = "",
    val value: Message
)

data class ParentNeo<T>(
    val key: String = "",
    val value: T
)