package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.Message
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.chat.notification.SendMessageDto
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getAllChatrooms(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>>
    suspend fun getChatroom(id: String): ApiResult<ChatRoom>
    suspend fun addChatroom(chatRoom: ChatRoom)
    fun getMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, message: Message)
    suspend fun updateChatroom(chatId: String, chatRoom: ChatRoom)
    suspend fun deleteChatroom(chatroom: ChatRoom)
    suspend fun subscribeToTopic(topic: String)
    suspend fun sendNotification(isBroadcast: Boolean, messageDto: SendMessageDto)
}