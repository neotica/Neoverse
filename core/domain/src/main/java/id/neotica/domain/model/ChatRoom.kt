package id.neotica.domain.model

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

sealed class ApiResult<T>(
    val data: T? = null,
    val errorMessage: String? = null
) {
    class Success<T>(data: T) : ApiResult<T>(data)
    class Loading<T>(data: T? = null) : ApiResult<T>(data)
    class Error<T>(errorMessage: String? = null) : ApiResult<T>(null, errorMessage)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorMessage]"
            is Loading<T> -> "Loading"
        }
    }
}