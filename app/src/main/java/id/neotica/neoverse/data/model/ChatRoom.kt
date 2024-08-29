package id.neotica.neoverse.data.model

data class ChatRoom(
    val id: String = "",
    val name: String = "",
    val user1: String = "",
    val user1id: String = "",
    val user1Read: Boolean = false,
    val user2: String = "",
    val user2id: String = "",
    val user2Read: Boolean = false,
    val lastOpened: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ParentChatRoom(
    val key: String = "",
    val value: ChatRoom
)