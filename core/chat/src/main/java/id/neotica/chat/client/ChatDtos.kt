package id.neotica.chat.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserRefDto(
    val id: String = "",
    val username: String = "",
    val avatarUrl: String? = null
)

@Serializable
internal data class ConversationDto(
    val id: String = "",
    val peer: UserRefDto = UserRefDto(),
    val lastMessage: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
internal data class ConversationListDto(
    val data: List<ConversationDto> = emptyList()
)

@Serializable
internal data class ChatMessageDto(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val body: String = "",
    val createdAt: Long = 0L
)

@Serializable
internal data class MessagePageDto(
    val data: List<ChatMessageDto> = emptyList(),
    val nextCursor: String? = null
)

@Serializable
internal data class CreateConversationRequest(
    val username: String
)

@Serializable
internal data class SendMessageRequest(
    val clientMessageId: String,
    val body: String
)

@Serializable
internal data class ChatSocketEvent(
    val type: String = "",
    val conversationId: String? = null,
    val message: ChatMessageDto? = null,
    val conversation: ConversationDto? = null
)

@Serializable
internal data class ErrorDto(
    val code: String = "",
    val message: String = ""
)

@Serializable
internal data class AuthSessionDto(
    @SerialName("token") val accessToken: String = "",
    val refreshToken: String = "",
    val expirationTime: Long = 0L
)
