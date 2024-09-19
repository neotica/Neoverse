package id.neotica.neoverse.presentation.screen.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.ParentNeo
import id.neotica.domain.model.User
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    private val databaseRepo: FriendDaoRepository,
    private val userDatabase: CurrentUserRepository
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private val _chatroom = MutableStateFlow<ApiResult<List<ParentNeo<ChatRoom>>>>(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private var _userEntity: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(
        ApiResult.Loading())
    val user = _userEntity.asStateFlow()

    private var _friendList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val friendList = _friendList.asStateFlow()

    fun currentUserId(): String = _userEntity.value.data?.userId ?: ""
    fun currentUserName(): String = _userEntity.value.data?.username ?: ""

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        val userData = userDatabase.getUser()
        userData.collect {
            if (it.isEmpty()) {
                _user.value.data?.let { remoteUser ->
                }
            } else {
                _userEntity.value = ApiResult.Success(it[0])
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun getFriendsRoom() = databaseRepo.getFriends()

    fun addFriendList(friends: List<User>) = viewModelScope.launch {
        databaseRepo.addFriendList(friends)
    }

    fun addFriend(friend: User) = viewModelScope.launch {
        userRepo.addFriend(friend)
        databaseRepo.addFriend(friend)
    }

    fun unFriend(friend: User) = viewModelScope.launch {
        userRepo.unfriend(friend)
        databaseRepo.unfriend(friend.username)
    }

    fun searchFriend(username: String) = viewModelScope.launch {
        val user = userRepo.searchUser(username)
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getFriends() = viewModelScope.launch {
        userRepo.getFriends().collect {
            _friendList.value = it
        }
    }

    fun getChatroom() = viewModelScope.launch {
        chatRepo.getChatroom().collect {
            _chatroom.value = it
        }
    }

    fun addChatroom(user2: String, user: User) = viewModelScope.launch {
        currentUserName().let {
            val chatroomDetail = ChatRoom(
                name = currentUserId()+ user.userId,
                user1 = it,
                user2 = user2,
                user1id = currentUserId(),
                user2id = user.userId
            )
            chatRepo.addChatroom(chatroomDetail)
        }
    }

    fun getRoomFiltered(user: String, chatroom: List<ChatRoom>): ChatRoom? {
        val find1 = chatroom.find {
            currentUserId()+user == it.name || user+currentUserId() == it.name
        }
        Log.d("isUserExist", "find: $find1")
        return find1
    }
}