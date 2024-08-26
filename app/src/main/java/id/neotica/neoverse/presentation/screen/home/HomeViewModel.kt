package id.neotica.neoverse.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.database
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.ChatRepository
import id.neotica.neoverse.data.repo.UserRepository
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.utils.getPartWithUser
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

    private val _chatroom = MutableStateFlow<ApiResult<List<ChatRoom>>>(ApiResult.Loading())
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
            Log.d("homevm", "getChatroom: $it")

        }
    }

    fun getUserChatroom(navController: NavController) = viewModelScope.launch {
        chatRepo.getChatroom().collect {
            it.data?.forEach { chatItems ->
                gotoChatRoom(chatItems.name, navController)
            }

            _chatroom.value = it
            Log.d("homevm", "getChatroom: $it")

        }
    }

    fun filterChatrooms(chatroom: List<ChatRoom>): List<ChatRoom> {
        return if (currentUserName() != null) {
            chatroom.filter { cr ->
                cr.name.isNotEmpty() && getPartWithUser(cr.name, currentUserName()!!) != "No match!"
            }
        } else {
            emptyList()
        }
    }

    fun gotoChatRoom(channel: String, navController: NavController) = viewModelScope.launch {
        currentUserName()?.let {
            val exactMatch = getPartWithUser(
                combinedRoomName = channel,
                current = it
            )
            if (exactMatch != "No match found") {
                navController.navigate(Screen.Chat(chatId = channel))
            }
            Log.d("hvm", "current: $it, exact: $exactMatch")
        }
    }

    fun chatroomName(channel: String): String {
        currentUserName()?.let {
            val exactMatch = getPartWithUser(
                combinedRoomName = channel,
                current = it
            )
            val channelParse = channel.replace(exactMatch, "").substringAfter(":").replace("and", "").trim()
            if (exactMatch != "No match found") {
                return channelParse
            }
            Log.d("hvm", "current: $it, exact: $exactMatch")
        }
        return ""
    }

    fun addChatroom(user2: String, chatrooms: List<ChatRoom>, navController: NavController) = viewModelScope.launch {
        if (currentUserName().isNullOrEmpty()) navController.navigate(Screen.LoginScreen)
        currentUserName()?.let { currentUser ->
            val combinedRoomName1 = "1:$currentUser and 2:$user2"
            val combinedRoomName2 = "1:$user2 and 2:$currentUser"

            val existingRoom = chatrooms.find { it.name == combinedRoomName1 || it.name == combinedRoomName2 }

            if (existingRoom != null) {
                navController.navigate(Screen.Chat(existingRoom.name))
            } else {
                chatRepo.addChatroom(combinedRoomName1)
                navController.navigate(Screen.Chat(combinedRoomName1))
            }
        }
    }
}