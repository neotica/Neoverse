package id.neotica.chat.client

import id.neotica.domain.session.SessionRepository
import id.neotica.neoverse.utils.chatWebSocketUrl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal class ChatRealtimeClient(
    private val client: HttpClient,
    private val session: SessionRepository,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _events = MutableSharedFlow<ChatSocketEvent>(extraBufferCapacity = 64)
    internal val events = _events.asSharedFlow()

    private var connectionJob: Job? = null

    fun start() {
        if (connectionJob?.isActive == true) return
        connectionJob = scope.launch { connectLoop() }
    }

    fun stop() {
        connectionJob?.cancel()
        connectionJob = null
    }

    private suspend fun connectLoop() {
        var backoff = 1_000L
        while (currentCoroutineContext().isActive) {
            val token = session.getAccessToken()
            if (token.isNullOrBlank()) {
                delay(2_000L)
                continue
            }

            var stopReconnecting = false
            try {
                client.webSocket(
                    urlString = chatWebSocketUrl,
                    request = {
                        header(HttpHeaders.Authorization, "Bearer $token")
                        timeout {
                            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                            connectTimeoutMillis = 15_000L
                        }
                    }
                ) {
                    backoff = 1_000L
                    _events.tryEmit(ChatSocketEvent(type = "connection.restored"))
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val event = runCatching {
                                json.decodeFromString<ChatSocketEvent>(frame.readText())
                            }.getOrNull()
                            if (event != null) {
                                _events.tryEmit(event)
                            }
                        }
                    }

                    when (runCatching { closeReason.await()?.code?.toInt() }.getOrNull()) {
                        4403 -> {
                            session.clearSession()
                            stopReconnecting = true
                        }
                        4401 -> session.refreshAccessToken()
                    }
                }
            } catch (_: Exception) {
                session.refreshAccessToken()
            }

            if (stopReconnecting) break
            delay(backoff)
            backoff = (backoff * 2).coerceAtMost(30_000L)
        }
    }
}
