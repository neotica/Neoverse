package id.neotica.neoverse.data.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderImage: String? = null,
    val imageUrl: String? = null
)
