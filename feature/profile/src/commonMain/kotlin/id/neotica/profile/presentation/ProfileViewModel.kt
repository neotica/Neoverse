package id.neotica.profile.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.model.User
import id.neotica.profile.domain.ProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository,
    private val friendDatabase: FriendDaoRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())

    private var _currentUser: MutableStateFlow<ApiResult<User>?> = MutableStateFlow(
        ApiResult.Loading()
    )
    val currentUser = _currentUser.asStateFlow()

    init {
        getUserRoomInstance()
    }

    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn()
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
        val user = profileRepo.getUserDetail(authRepo.getUserId().toString())
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

    fun changeProfilePic(uri: String) = CoroutineScope(Dispatchers.IO).launch {
        getUser()
        profileRepo.changeProfilePicture(uri)
        currentUserDatabase.getUser().collect {
            val db = it[0]
            val tempUser = User(
                userId = db.userId,
                username = db.username,
                userEmail = db.userEmail,
                status = db.status,
                imageUrl = profileRepo.getUserProfilePic()
            )
            profileRepo.changeUserProfile(tempUser)
            currentUserDatabase.saveUser(tempUser)
            getUser()
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

    fun resetPassword(email: String) = viewModelScope.launch {
        authRepo.resetPassword(email)
    }
}