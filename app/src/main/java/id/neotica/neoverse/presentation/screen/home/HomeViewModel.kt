package id.neotica.neoverse.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.model.User
import id.neotica.domain.model.chat.ChatRoom
import id.neotica.domain.model.chat.ParentNeo
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository,
    private val userDatabase: CurrentUserRepository,
    private val friendDatabase: FriendDaoRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val auraRepo: AuraRepository,
    private val adminRepo: SuperAdminRepository,
): ViewModel() {
    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private val _versionCode: MutableStateFlow<ApiResult<Int>> = MutableStateFlow(ApiResult.Loading())
    val versionCode = _versionCode.asStateFlow()

    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private var _userEntity: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(
        ApiResult.Loading())
    val user = _userEntity.asStateFlow()

    private val _chatroom: MutableStateFlow<ApiResult<List<ParentNeo<ChatRoom>>>> = MutableStateFlow(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private var _friendList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val friendList = _friendList.asStateFlow()

    private val _chatList = mutableListOf<ParentNeo<ChatRoom>>()
    val chatList: List<ParentNeo<ChatRoom>> = _chatList

    init {
        getUser()
        getChatroom()
        fetchAuras()
        getMinimumVersion()
    }

    private fun currentUserId() = getUserIdUseCase.invoke()

    fun isLoggedIn(): Boolean = currentUserId().isNotEmpty()

    fun isUserVerified() = userRepo.isUserVerified()

    fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAurasDecrypt(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    private fun getMinimumVersion() = viewModelScope.launch {
        adminRepo.getMinimumVersion().collect {
            _versionCode.value = it
        }
    }

    private fun getUser() = viewModelScope.launch {
        val userData = userDatabase.getUser()
        if (isLoggedIn()) {
            userData.collect {
                if (it.isEmpty()) {
                    getUserOnline()
                } else {
                    _userEntity.value = ApiResult.Success(it[0])
                }
            }
        }
    }

    private fun getUserOnline() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = user
        _user.value.data?.let { remoteUser ->
            userDatabase.saveUser(remoteUser)
        }
    }

    fun getChatroom() = viewModelScope.launch {
        _chatList.clear()
        chatRepo.getAllChatrooms().collect {
            _chatroom.value = it
            it.data?.forEach { list ->
                _chatList.add(list)
            }
        }
    }

    fun getFriendsRoom() = friendDatabase.getFriends()

    fun getFriends() = viewModelScope.launch {
        _friendList.value = ApiResult.Loading()
        userRepo.getFriends().collect {
            _friendList.value = it
        }
    }

    fun addFriendList(friends: List<User>) = viewModelScope.launch {
        friendDatabase.addFriendList(friends)
    }

    fun deleteChatroom(chatroom: ChatRoom) = viewModelScope.launch {
        getChatroom()
        getUser()
        chatRepo.deleteChatroom(chatroom)
    }
}