package id.neotica.neoverse.presentation.screen.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.model.Friends
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendViewModel(
    private val userRepo: UserRepository
): ViewModel() {

    private var _userList: MutableStateFlow<ApiResult<List<User>>> = MutableStateFlow(ApiResult.Loading())
    val userList = _userList.asStateFlow()

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    private var _friendList: MutableStateFlow<ApiResult<List<Friends>>> = MutableStateFlow(ApiResult.Loading())
    val friendList = _friendList.asStateFlow()

    fun getCurrentId(): String = userRepo.userId()

    fun addFriend(friend: Friends) = viewModelScope.launch {
        userRepo.addFriend(friend)
    }

    fun searchFriend(username: String) = viewModelScope.launch {
        val user = userRepo.searchUser(username)
        _userList.value = ApiResult.Loading()
        _userList.value = user
    }

    fun getUser(userId: String) = viewModelScope.launch {
        val user = userRepo.getUserDetail(userId)
        _user.value = ApiResult.Loading()
        _user.value = user
    }

    fun getFriends() = viewModelScope.launch {
//        _friendList.value = userRepo.getFriends()
        userRepo.getFriends().collect {
            _friendList.value = it
        }
    }
}