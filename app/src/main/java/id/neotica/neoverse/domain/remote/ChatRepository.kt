package id.neotica.neoverse.domain.remote

import id.neotica.neoverse.data.remote.model.ChatRoom
import id.neotica.neoverse.data.remote.model.Message
import id.neotica.neoverse.data.remote.model.ParentNeo
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatroom(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>>
    suspend fun addChatroom(name: ChatRoom)
    fun getMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, message: Message)
    suspend fun readMessage(chatId: String, chatRoom: ChatRoom)
    suspend fun deleteChatroom(name: String)
}