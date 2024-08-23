package id.neotica.neoverse.presentation.screen.profile

import androidx.lifecycle.ViewModel
import id.neotica.neoverse.data.repo.AuthRepository

class ProfileViewModel(
    private val authRepo: AuthRepository
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn()
    }

    fun logout() {
        authRepo.logout()
    }
}