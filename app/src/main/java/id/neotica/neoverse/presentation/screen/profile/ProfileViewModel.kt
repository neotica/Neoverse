package id.neotica.neoverse.presentation.screen.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.model.User
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

    fun changeProfilePic(uri: Uri) = viewModelScope.launch {
        getUser()
        userRepo.changeProfilePicture(uri)
        currentUserDatabase.getUser().collect {
            val db = it[0]
            val tempUser = User(
                userId = db.userId,
                username = db.username,
                userEmail = db.userEmail,
                status = db.status,
                imageUrl = userRepo.getUserProfilePic().toString()
            )
            userRepo.changeUserProfile(tempUser)
        }
    }

    val visiblePermissionQueue = mutableStateListOf<String>()

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            visiblePermissionQueue.add(0, permission)
        }
    }
}