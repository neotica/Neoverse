package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatMessage
import id.neotica.domain.model.chat.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun getConversations(): ApiResult<List<Conversation>>
    suspend fun getOrCreateConversation(username: String): ApiResult<Conversation>
    fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(
        conversationId: String,
        clientMessageId: String,
        body: String
    ): ApiResult<ChatMessage>
    suspend fun deleteConversation(conversationId: String): ApiResult<Unit>
    suspend fun deleteMyChatData(): ApiResult<Unit>
}
