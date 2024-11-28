package id.neotica.domain.model.chat
import id.neotica.neoverse.getCurrentTimeMillis

data class ChatRoom(
    val id: String = "",
    val name: String = "",
    val user1: String = "",
    val user1id: String = "",
    val user1Read: Boolean = false,
    val user2: String = "",
    val user2id: String = "",
    val user2Read: Boolean = false,
    val lastMessage: String = "",
    val lastOpened: Long = getCurrentTimeMillis().time,
    val createdAt: Long = getCurrentTimeMillis().time
)

data class ParentChatRoom(
    val key: String = "",
    val value: ChatRoom
)