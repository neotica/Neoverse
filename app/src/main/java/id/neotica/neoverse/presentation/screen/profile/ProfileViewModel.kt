package id.neotica.neoverse.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.data.repo.AuthRepositoryImpl
import id.neotica.neoverse.data.repo.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepo: AuthRepositoryImpl,
    private val userRepo: UserRepository
): ViewModel() {

    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn()
    }

    fun logout() {
        authRepo.logout()
    }

    private var _user: MutableStateFlow<ApiResult<User?>> = MutableStateFlow(ApiResult.Loading())
    val user = _user.asStateFlow()

    fun getUser() = viewModelScope.launch {
        val user = userRepo.getUserDetail()
        _user.value = ApiResult.Loading()
        _user.value = user
    }
}