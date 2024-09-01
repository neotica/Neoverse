package id.neotica.neoverse.domain

import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Message
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatroom(): Flow<ApiResult<List<ParentNeo<ChatRoom>>>>
    suspend fun addChatroom(name: ChatRoom)
    fun getMessages(chatId: String): Flow<List<Message>>
    fun sendMessage(chatId: String, message: Message)
    suspend fun readMessage(chatId: String, chatRoom: ChatRoom)
}