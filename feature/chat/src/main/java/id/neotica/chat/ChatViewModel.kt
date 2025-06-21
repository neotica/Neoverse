package id.neotica.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.Message
import id.neotica.domain.model.chat.notification.NotificationBody
import id.neotica.domain.model.chat.notification.SendMessageDto
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.routes.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepo: ChatRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val args = savedStateHandle.toRoute<Screen.Chat>()
    val message = chatRepo.getMessages(args.chatId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _chatroom: MutableStateFlow<ApiResult<ChatRoom>> = MutableStateFlow(ApiResult.Loading())

    init {
        getChatroom()
        viewModelScope.launch {
            chatRepo.subscribeToTopic("chat")
        }
    }

    fun getUserId() = getUserIdUseCase.invoke()

    private fun getChatroom() = viewModelScope.launch {
        val get = chatRepo.getChatroom(args.chatroomId.toString())
        _chatroom.value = get
    }

    fun sendMessage(chatId: String, chat: String) = viewModelScope.launch {
        val messageValue = Message(
            id = chatRepo.pushReferenceKey(),
            senderId = getUserIdUseCase.invoke(),
            message = chat,
            createdAt = System.currentTimeMillis(),
            senderImage = null,
            imageUrl = null,
        )
        chatRepo.sendMessage(chatId, messageValue)
        _chatroom.collect { result ->
            if (result is ApiResult.Success) {
                if (result.data?.id != "") {
                    val chatNow = result.data?.copy(
                        lastOpened = messageValue.createdAt,
                        lastMessage = "${getUserNameUseCase.invoke()}: ${messageValue.message}",
                    )
                    chatRepo.updateChatroom(result.data?.id!!, chatNow!!)
                }
                val sendMessageDto = SendMessageDto(
                    to = if (_chatroom.value.data == null) null else _chatroom.value.data?.fcmToken,
                    notification = NotificationBody(
                        title = getUserNameUseCase.invoke(),
                        body = chat
                    )
                )
                sendNotification(sendMessageDto)
            }
        }
    }

    fun deleteChatroom() = CoroutineScope(Dispatchers.IO).launch {
        _chatroom.value.data?.let { chatRepo.deleteChatroom(it) }
    }

    private fun sendNotification(messageDto: SendMessageDto) = viewModelScope.launch {
        chatRepo.sendNotification(
            isBroadcast = messageDto.to != null,
            messageDto = messageDto
        )
    }
}