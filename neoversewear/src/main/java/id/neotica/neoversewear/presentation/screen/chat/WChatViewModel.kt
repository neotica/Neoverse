package id.neotica.neoversewear.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.Message
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.routes.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class WChatViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    private val currentUserDatabase: CurrentUserRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val args = savedStateHandle.toRoute<Screen.Chat>()
    val message = chatRepo.getMessages(args.chatId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val db = Firebase.database

    private val _chatroom: MutableStateFlow<ApiResult<ChatRoom>> = MutableStateFlow(ApiResult.Loading())

    private var _currentUser: MutableStateFlow<ApiResult<User>?> = MutableStateFlow(
        ApiResult.Loading())

    init {
        getChatroom()
        getUserRoomInstance()
    }

    private fun getUserRoomInstance() = viewModelScope.launch {
        currentUserDatabase.getUser().collect {
            if (it.isNotEmpty()) {
                _currentUser.value = ApiResult.Success(it[0])
            }
        }
    }

    private fun getChatroom() = viewModelScope.launch {
        Log.d("chtrm vm", "inside")
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
        updateLastMessage(chat)
    }

    private fun updateLastMessage(message: String) = viewModelScope.launch {

        _chatroom.value.data?.let {
            if (it.id != "") {
                currentUserDatabase.getUser().collect { currentUser ->
                    val chat = ChatRoom(
                        id = it.id,
                        name = it.name,
                        user1 = it.user1,
                        user2 = it.user2,
                        user1Read = it.user1Read,
                        user2Read = it.user2Read,
                        user1id = it.user1id,
                        user2id = it.user2id,
                        createdAt = it.createdAt,
                        lastMessage = "${currentUser[0].username}: $message",
                    )
                    chatRepo.updateChatroom(it.id, chat)
                }
            }
        }
    }
}