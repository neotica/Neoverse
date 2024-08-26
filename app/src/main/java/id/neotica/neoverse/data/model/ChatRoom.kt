package id.neotica.neoverse.data.model

data class ChatRoom(
    val id: String = "",
    val name: String = "",
//    val owner: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
