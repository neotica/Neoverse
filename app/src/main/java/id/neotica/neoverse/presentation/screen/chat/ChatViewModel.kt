package id.neotica.neoverse.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.Message
import id.neotica.domain.model.chat.notification.NotificationBody
import id.neotica.domain.model.chat.notification.SendMessageDto
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    private val getUserNameUseCase: GetUserNameUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val args = savedStateHandle.toRoute<Screen.Chat>()
    val message = chatRepo.getMessages(args.chatId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val db = Firebase.database

    private val _chatroom: MutableStateFlow<ApiResult<ChatRoom>> = MutableStateFlow(ApiResult.Loading())

    init {
        getChatroom()
        viewModelScope.launch {
            chatRepo.subscribeToTopic("chat")
        }
    }

    private fun getChatroom() = viewModelScope.launch {
        val get = chatRepo.getChatroom(args.chatroomId.toString())
        _chatroom.value = get
    }

    fun sendMessage(chatId: String, chat: String) {
        val messageValue = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = userRepo.getUserId(),
            receiverId = Firebase.auth.currentUser?.uid ?: "",
            message = chat,
            createdAt = System.currentTimeMillis(),
            senderImage = null,
            imageUrl = null
        )
        chatRepo.sendMessage(chatId, messageValue)
        updateLastMessage(messageValue)
        val sendMessageDto = SendMessageDto(
            to = if (_chatroom.value.data == null) null else _chatroom.value.data?.fcmToken,
            notification = NotificationBody(
                title = getUserNameUseCase.invoke(),
                body = chat
            )
        )
        sendNotification(sendMessageDto)
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

    private fun updateLastMessage(message: Message) = viewModelScope.launch {
        _chatroom.value.data?.let {
            if (it.id != "") {
                val chat = it.copy(
                    lastOpened = message.createdAt,
                    lastMessage = "${getUserNameUseCase.invoke()}: ${message.message}",
                )
                chatRepo.updateChatroom(it.id, chat)
            }
        }
    }
}