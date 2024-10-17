package id.neotica.neoverse.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.model.ChatRoom
import id.neotica.domain.model.ParentNeo
import id.neotica.domain.model.User
import id.neotica.domain.remote.ChatRepository
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
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private var _userEntity: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(
        ApiResult.Loading())
    val user = _userEntity.asStateFlow()

    private val _chatroom: MutableStateFlow<ApiResult<List<ParentNeo<ChatRoom>>>> = MutableStateFlow(ApiResult.Loading())
    val chatroom = _chatroom.asStateFlow()

    private val _chatList = mutableListOf<ParentNeo<ChatRoom>>()
    val chatList: List<ParentNeo<ChatRoom>> = _chatList

    init {
        getUser()
        getChatroom()
    }

    fun isLoggedIn(): Boolean {
        return getUserIdUseCase.invoke().isNotEmpty()
    }

    fun isUserVerified() = userRepo.isUserVerified()

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

    fun deleteChatroom(chatroom: ChatRoom) = viewModelScope.launch {
        getChatroom()
        getUser()
        chatRepo.deleteChatroom(chatroom)
    }
}