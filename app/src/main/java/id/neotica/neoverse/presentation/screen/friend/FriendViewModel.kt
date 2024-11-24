package id.neotica.neoverse.presentation.screen.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.model.User
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    private val databaseRepo: FriendDaoRepository,
    private val userDatabase: CurrentUserRepository,
    private val getUserIdUseCase: GetUserIdUseCase
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

    private var _friendProfile: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val friendProfile = _friendProfile.asStateFlow()

    fun currentUserId(): String = _userEntity.value.data?.userId ?: ""
    private fun currentUserName(): String = _userEntity.value.data?.username ?: ""

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
        return getUserIdUseCase.invoke().isNotEmpty()
    }

    fun getFriendsRoom() = databaseRepo.getFriends()

    fun addFriendList(friends: List<User>) = viewModelScope.launch {
        databaseRepo.addFriendList(friends)
    }

    // TODO: Add stateFlow for addFriend and unFriend, use viewModelScope.
    fun addFriend(friend: User) = CoroutineScope(Dispatchers.IO).launch {
        userRepo.addFriend(friend)
        databaseRepo.addFriend(friend)
    }

    fun getFriendProfile(userId: String) = viewModelScope.launch {
        val user = userRepo.getUserDetail(userId)
        _friendProfile.value = ApiResult.Loading()
        _friendProfile.value = user
    }

    fun unFriend(friend: User) = CoroutineScope(Dispatchers.IO).launch {
        userRepo.unfriend(friend)
        databaseRepo.unfriend(friend.username)
    }

    fun searchFriend(username: String) = viewModelScope.launch {
        val user = userRepo.searchUser(username)
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getFriends() = viewModelScope.launch {
        _friendList.value = ApiResult.Loading()
        userRepo.getFriends().collect {
            _friendList.value = it
        }
    }

    fun getChatroom() = viewModelScope.launch {
        chatRepo.getAllChatrooms().collect {
            _chatroom.value = it
        }
    }

    fun addChatroom(user2: String, user: User) = viewModelScope.launch {
        currentUserName().let {
            val chatroomDetail = ChatRoom(
                id = randomUUID(),
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