package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.Message
import id.neotica.domain.model.ParentNeo
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getAllChatrooms(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>>
    suspend fun getChatroom(id: String): ApiResult<ChatRoom>
    suspend fun addChatroom(name: ChatRoom)
    fun getMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, message: Message)
    suspend fun updateChatroom(chatId: String, chatRoom: ChatRoom)
    suspend fun deleteChatroom(chatroom: ChatRoom)
}