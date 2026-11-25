package id.neotica.chat.client

import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatMessage
import id.neotica.domain.model.chat.Conversation
import id.neotica.domain.remote.ConversationRepository
import id.neotica.domain.session.SessionRepository
import id.neotica.neoverse.utils.chatBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal class ConversationRepositoryImpl(
    private val client: HttpClient,
    private val session: SessionRepository,
    private val realtime: ChatRealtimeClient,
) : ConversationRepository {

    private val localMessages = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 32)

    override suspend fun getConversations(): ApiResult<List<Conversation>> = try {
        val response = authorizedRequest { token ->
            client.get("$chatBaseUrl/v1/conversations") {
                bearer(token)
            }
        }
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<ConversationListDto>().data.map { it.toDomain() })
        } else {
            ApiResult.Error(response.errorMessage())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to load conversations")
    }

    override suspend fun getOrCreateConversation(username: String): ApiResult<Conversation> = try {
        val response = authorizedRequest { token ->
            client.post("$chatBaseUrl/v1/conversations") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(CreateConversationRequest(username))
            }
        }
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<ConversationDto>().toDomain())
        } else {
            ApiResult.Error(response.errorMessage())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to create conversation")
    }

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> = channelFlow {
        val initial = fetchMessages(conversationId)
        val messages = initial.toMutableList()
        send(messages.toList())

        fun append(incoming: ChatMessage) {
            if (incoming.conversationId != conversationId) return
            if (messages.any { it.id == incoming.id }) return
            messages.add(incoming)
            messages.sortBy { it.createdAt }
            trySend(messages.toList())
        }

        launch {
            localMessages.collect { append(it) }
        }

        realtime.start()
        realtime.events.collect { event ->
            if (event.type == "connection.restored") {
                val latestId = messages.lastOrNull()?.id
                fetchMessages(conversationId, after = latestId).forEach(::append)
            } else {
                event.message?.toDomain()?.let(::append)
            }
        }
    }

    override suspend fun sendMessage(
        conversationId: String,
        clientMessageId: String,
        body: String,
    ): ApiResult<ChatMessage> = try {
        val response = authorizedRequest { token ->
            client.post("$chatBaseUrl/v1/conversations/$conversationId/messages") {
                bearer(token)
                contentType(ContentType.Application.Json)
                setBody(SendMessageRequest(clientMessageId, body))
            }
        }
        if (response.status.isSuccess()) {
            val message = response.body<ChatMessageDto>().toDomain()
            localMessages.tryEmit(message)
            ApiResult.Success(message)
        } else {
            ApiResult.Error(response.errorMessage())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to send message")
    }

    override suspend fun deleteConversation(conversationId: String): ApiResult<Unit> = try {
        val response = authorizedRequest { token ->
            client.delete("$chatBaseUrl/v1/conversations/$conversationId") {
                bearer(token)
            }
        }
        if (response.status.isSuccess()) ApiResult.Success(Unit)
        else ApiResult.Error(response.errorMessage())
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to delete conversation")
    }

    override suspend fun deleteMyChatData(): ApiResult<Unit> = try {
        val response = authorizedRequest { token ->
            client.delete("$chatBaseUrl/v1/me") {
                bearer(token)
            }
        }
        if (response.status.isSuccess()) ApiResult.Success(Unit)
        else ApiResult.Error(response.errorMessage())
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to delete chat data")
    }

    private suspend fun fetchMessages(
        conversationId: String,
        after: String? = null,
    ): List<ChatMessage> = try {
        val response = authorizedRequest { token ->
            client.get("$chatBaseUrl/v1/conversations/$conversationId/messages") {
                bearer(token)
                parameter("limit", 50)
                if (!after.isNullOrBlank()) parameter("after", after)
            }
        }
        if (response.status.isSuccess()) {
            response.body<MessagePageDto>().data.map { it.toDomain() }.sortedBy { it.createdAt }
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }

    private suspend fun authorizedRequest(
        request: suspend (String) -> HttpResponse,
    ): HttpResponse {
        val token = session.getAccessToken() ?: error("Not authenticated")
        val first = request(token)
        if (first.status != HttpStatusCode.Unauthorized) return first
        val refreshed = session.refreshAccessToken() ?: return first
        return request(refreshed)
    }

    private fun io.ktor.client.request.HttpRequestBuilder.bearer(token: String) {
        header(HttpHeaders.Authorization, "Bearer $token")
    }

    private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299

    private suspend fun HttpResponse.errorMessage(): String = try {
        body<ErrorDto>().message.ifBlank { "HTTP ${status.value}" }
    } catch (_: Exception) {
        "HTTP ${status.value}"
    }
}

private fun ConversationDto.toDomain() = Conversation(
    id = id,
    peerId = peer.id,
    peerUsername = peer.username,
    peerAvatarUrl = peer.avatarUrl,
    lastMessage = lastMessage.orEmpty(),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun ChatMessageDto.toDomain() = ChatMessage(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    body = body,
    createdAt = createdAt,
)
