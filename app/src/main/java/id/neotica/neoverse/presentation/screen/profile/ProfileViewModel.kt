package id.neotica.neoverse.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import id.neotica.domain.model.User
import id.neotica.domain.ApiResult
import id.neotica.neoverse.data.local.domain.local.CurrentUserRepository
import id.neotica.neoverse.data.local.domain.local.FriendDaoRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val friendDatabase: FriendDaoRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private var _currentUser: MutableStateFlow<ApiResult<User>?> = MutableStateFlow(
        ApiResult.Loading())
    val currentUser = _currentUser.asStateFlow()

    init {
        getUserRoomInstance()
    }

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun logout() = viewModelScope.launch {
        authRepo.logout()
        currentUserDatabase.clearCurrent()
        friendDatabase.clearFriends()
    }

    fun deleteAccount() = viewModelScope.launch {
        authRepo.deleteAccount()
        friendDatabase.clearFriends()
        currentUserDatabase.clearCurrent()
    }

    private fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
        user.data?.let { currentUserDatabase.saveUser(it) }
    }

    private fun getUserRoomInstance() = viewModelScope.launch {
        currentUserDatabase.getUser().collect {
            if (it.isNotEmpty()) {
                _currentUser.value = ApiResult.Success(it[0])
            } else {
                getUser()
            }
        }
    }
}