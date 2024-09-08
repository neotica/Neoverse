package id.neotica.neoverse.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.local.repo.CurrentUserRepositoryImpl
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.local.FriendDaoRepository
import id.neotica.neoverse.domain.remote.AuthRepository
import id.neotica.neoverse.domain.remote.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val friendDatabase: FriendDaoRepository,
    private val currentUserDatabase: CurrentUserRepositoryImpl
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun logout() = viewModelScope.launch {
        authRepo.logout()
        friendDatabase.clearFriends()
        currentUserDatabase.clearCurrent()
    }

    fun deleteAccount() = viewModelScope.launch {
        authRepo.deleteAccount()
        friendDatabase.clearFriends()
        currentUserDatabase.clearCurrent()
    }

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
    }

    fun saveUserRoom(user: User) = viewModelScope.launch {
        currentUserDatabase.saveUser(user)
    }

    fun getUserRoom() = currentUserDatabase.getUser()
}