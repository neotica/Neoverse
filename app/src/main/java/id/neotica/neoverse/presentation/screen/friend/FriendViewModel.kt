package id.neotica.neoverse.presentation.screen.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.model.ChatRoom
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.ParentNeo
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.ChatRepository
import id.neotica.neoverse.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private val _chatroom = MutableStateFlow<ApiResult<List<ParentNeo<ChatRoom>>>>(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    private var _friendList: MutableStateFlow<ApiResult<List<Friends>>> = MutableStateFlow(ApiResult.Loading())
    val friendList = _friendList.asStateFlow()

    val isOk = MutableStateFlow<Boolean>(false)

    fun currentUserId(): String = userRepo.userId()
    fun currentUserName(): String? = _user.value.data?.username

    fun addFriend(friend: Friends) = viewModelScope.launch {
        userRepo.addFriend(friend)
    }

    fun searchFriend(username: String) = viewModelScope.launch {
        val user = userRepo.searchUser(username)
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getUser(userId: String = "") = viewModelScope.launch {
        Log.d("getUser", "userId: $userId" +
                "\n" +
                "bool; ${userId != ""}")
        if (userId != "") {
            val user = userRepo.getUserDetail(userId)
            _user.value = ApiResult.Loading()
            _user.value = user
            Log.d("getUser", "notnull")
        } else {
            Log.d("getUser", "null")

            _user.value = userRepo.getUserDetail()
        }
    }

    fun getFriends() = viewModelScope.launch {
        userRepo.getFriends().collect {
            _friendList.value = it
        }
    }

    fun getFriends2() = viewModelScope.launch {
        getChatroom()
        _chatroom.value.data?.forEach {
            val name = it.value.name
        }
    }

    fun getChatroom() = viewModelScope.launch {
        chatRepo.getChatroom().collect {
            _chatroom.value = it
        }
    }

    fun addChatroom(user2: String, user: User) = viewModelScope.launch {
        Log.d("friendvm", currentUserName().toString())
        currentUserName()?.let {
            val chatroomDetail = ChatRoom(
                name = currentUserId()+user.userId.toString(),
                user1 = it,
                user2 = user2,
                user1id = currentUserId(),
                user2id = user.userId.toString()
            )
            chatRepo.addChatroom(chatroomDetail)
        }

    }

    fun getRoomFiltered(user: String, chatroom: List<ChatRoom>): ChatRoom? {
        val name = "kingkong"
        val find1 = chatroom.find {
            Log.d("isUserExist", it.name)

            currentUserId()+user == it.name || user+currentUserId() == it.name
        }
        val find2 = chatroom.find {
            it.user1 == name
        }
        val user1 = find1?.user1
        val check = if (user1 == name) true else false
        Log.d("isUserExist", "find: $find1")
        return find1
    }
}