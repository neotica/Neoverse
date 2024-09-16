package id.neotica.neoverse.presentation.screen.auth.verify

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.UserRepository

class VerifyEmailViewModel(
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository
): ViewModel() {
    private val auth = Firebase.auth

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }
    fun isUserVerified() = userRepo.isUserVerified()
    fun sendEmailVerification() = auth.currentUser?.sendEmailVerification()
    fun logout() = authRepo.logout()
}