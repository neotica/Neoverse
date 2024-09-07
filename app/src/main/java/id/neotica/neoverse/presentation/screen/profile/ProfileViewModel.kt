package id.neotica.neoverse.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.domain.AuthRepository
import id.neotica.neoverse.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return userRepo.isLoggedIn()
    }

    fun logout() {
        authRepo.logout()
    }

    fun deleteAccount() = viewModelScope.launch {
        authRepo.deleteAccount()
    }

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail(userRepo.getUserId())
        _user.value = ApiResult.Loading()
        _user.value = user
    }
}