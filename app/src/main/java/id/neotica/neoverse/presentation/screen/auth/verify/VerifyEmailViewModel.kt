package id.neotica.neoverse.presentation.screen.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
    private val auth = Firebase.auth

    fun isUserVerified() = userRepo.isUserVerified()
    fun sendEmailVerification() = auth.currentUser?.sendEmailVerification()
    fun logout() = viewModelScope.launch {
        currentUserDatabase.clearCurrent()
        friendDatabase.clearFriends()
        authRepo.logout()
    }
}