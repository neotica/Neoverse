package id.neotica.world.feat.world

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorldViewModel(
    private val gameRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val adminRepo: SuperAdminRepository
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()

    init {
        fetchAuras()
        getMinimumVersion()
    }

    private val _versionCode: MutableStateFlow<ApiResult<Int>> = MutableStateFlow(ApiResult.Loading())
    val versionCode = _versionCode.asStateFlow()

    private fun getMinimumVersion() = viewModelScope.launch {
        adminRepo.getMinimumVersion().collect {
            _versionCode.value = it
        }
    }

    fun fetchAuras() = viewModelScope.launch {
        gameRepo.getAurasDecrypt(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    fun sendInitialAura(aura: Long) = viewModelScope.launch {
        gameRepo.postAuras(aura, currentUserId())
        fetchAuras()
    }

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }
}