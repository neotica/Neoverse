package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.remote.ChatRepository
import id.neotica.neoverse.domain.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    private val _chatroom = MutableStateFlow<ApiResult<List<ParentNeo<ChatRoom>>>>(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private fun currentUserId(): String = userRepo.getUserId()
    private fun currentUserName(): String? = _user.value.data?.username

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun isUserVerified() = userRepo.isUserVerified()

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(currentUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
    }

    fun getUsers() = viewModelScope.launch {
        val user = userRepo.getAllUsers()
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getChatroom() = viewModelScope.launch {
        chatRepo.getChatroom().collect {
            _chatroom.value = it
        }
    }

    fun isRoomExist(user: String, chatroom: List<ChatRoom>): Boolean {
        val name = currentUserName()
        val find1 = chatroom.find {
            it.user1 == name || it.user2 == name
        }
        val find2 = chatroom.find {
            it.user1 == name
        }
        val user1 = find1?.user1
        val check = if (user == name) true else false
        Log.d("isUserExist", "check: $chatroom")
        return check
    }

    fun getRoomFiltered(user: String, chatroom: List<ChatRoom>): ChatRoom? {
        val find1 = chatroom.find {

            currentUserId()+user == it.name || user+currentUserId() == it.name
        }
        Log.d("isUserExist", "find: $find1")
        return find1
    }

    fun readMessage(chatId: String, chatRoom: ChatRoom, currentUser: String) = viewModelScope.launch {
        if (currentUser != chatRoom.user2) {
            val chatRoomRead = ChatRoom(
                id = chatId,
                name = chatRoom.name,
                user1 = chatRoom.user1,
                user1id = chatRoom.user1id,
                user1Read = true,
                user2 = chatRoom.user2,
                user2id = chatRoom.user2id,
                user2Read = false,
                createdAt = chatRoom.createdAt
            )
            chatRepo.readMessage(chatId, chatRoomRead)
        } else {
            val chatRoomRead = ChatRoom(
                id = chatId,
                name = chatRoom.name,
                user1 = chatRoom.user1,
                user1id = chatRoom.user1id,
                user1Read = false,
                user2 = chatRoom.user2,
                user2id = chatRoom.user2id,
                user2Read = true,
                createdAt = chatRoom.createdAt
            )
            chatRepo.readMessage(chatId, chatRoomRead)
        }
    }

    fun addChatroom(user2: String, user: User) = viewModelScope.launch {
        currentUserName()?.let { currentUser ->
            val chatroomDetail = ChatRoom(
                name = currentUserId()+user.userId.toString(),
                user1 = currentUser,
                user2 = user2,
                user1id = currentUserId(),
                user2id = user.userId.toString()
            )
            chatRepo.addChatroom(chatroomDetail)
        }
    }
}