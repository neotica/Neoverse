package id.neotica.neoverse.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.observer.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepo: AuthRepository,
    private val adminRepo: SuperAdminRepository,
    connectionObserver: ConnectivityObserver
): ViewModel() {

    private val _versionCode: MutableStateFlow<ApiResult<Int>> = MutableStateFlow(ApiResult.Loading())
    val versionCode = _versionCode.asStateFlow()

    init {
        getMinimumVersion()
    }

    val isConnected = connectionObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    private fun getMinimumVersion() = viewModelScope.launch {
        adminRepo.getMinimumVersion().collect {
            _versionCode.value = it
        }
    }
}