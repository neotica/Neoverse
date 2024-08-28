package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.ParentChatRoom
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.ChatRepository
import id.neotica.neoverse.data.repo.UserRepository
import id.neotica.neoverse.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    private val _chatroom = MutableStateFlow<ApiResult<List<ParentChatRoom>>>(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private val firebaseDatabase = Firebase.database


    fun currentUser(): String = userRepo.userId()

    private fun currentUserName(): String? = _user.value.data?.username

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail()
        _user.value = ApiResult.Loading()
        _user.value = user
    }

    fun getUsers() = viewModelScope.launch {
        val user = userRepo.getUsers()
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getChatroom() = viewModelScope.launch {
        chatRepo.getChatroom().collect {
            _chatroom.value = it
            Log.d("homevm", "getChatroom: ${it}")
        }
    }

    fun readMessage(chatId: String, chatRoom: ChatRoom, currentUser: String) = viewModelScope.launch {
        if (currentUser != chatRoom.user2) {
            val chatRoomRead = ChatRoom(
                id = chatId,
                name = chatRoom.name,
                user1 = chatRoom.user1,
                user1Read = true,
                user2 = chatRoom.user2,
                user2Read = false,
                createdAt = chatRoom.createdAt
            )
            chatRepo.readMessage(chatId, chatRoomRead)
        } else {
            val chatRoomRead = ChatRoom(
                id = chatId,
                name = chatRoom.name,
                user1 = chatRoom.user1,
                user1Read = false,
                user2 = chatRoom.user2,
                user2Read = true,
                createdAt = chatRoom.createdAt
            )
            chatRepo.readMessage(chatId, chatRoomRead)
        }
    }


    /*fun filterChatrooms(chatroom: List<ChatRoom>): List<ChatRoom> {
        return if (currentUserName() != null) {
            chatroom.filter { cr ->
                cr.name.isNotEmpty() && getPartWithUser(cr.name, currentUserName()!!) != "No match!"
            }
        } else {
            emptyList()
        }
    }*/

    fun addChatroom(user2: String, chatrooms: List<ChatRoom>, navController: NavController) = viewModelScope.launch {
        if (currentUserName().isNullOrEmpty()) navController.navigate(Screen.LoginScreen)
        currentUserName()?.let { currentUser ->

            val chatroom1 = chatrooms.find {
                currentUser+user2 == it.user1+it.user2 || currentUser+user2 == it.user2+it.user1
            }

            if (chatroom1 != null) {
                navController.navigate(Screen.Chat(chatroom1.name, chatroom1.user2))
            } else {
                val chatroomDetail = ChatRoom(
                    name = currentUser+user2,
                    user1 = currentUser,
                    user2 = user2
                )
                chatRepo.addChatroom(chatroomDetail)
                navController.navigate(Screen.Chat(currentUser+user2, user2))
            }
        }
    }
}