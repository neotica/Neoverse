package id.neotica.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.UserRepository
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository,
    private val friendDatabase: FriendDaoRepository,
    private val currentUserDatabase: CurrentUserRepository
): ViewModel() {

    fun isUserVerified() = userRepo.isUserVerified()
    fun sendEmailVerification() = authRepo.sendEmailVerification()
    fun logout() = viewModelScope.launch {
        currentUserDatabase.clearCurrent()
        friendDatabase.clearFriends()
        authRepo.logout()
    }
}