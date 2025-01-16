package id.neotica.world.feat.superadmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SuperAdminViewModel(
    private val auraRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val adminRepo: SuperAdminRepository,
    private val firebase: FirebaseRepository
): ViewModel() {

    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _versionCode: MutableStateFlow<ApiResult<Int>> = MutableStateFlow(ApiResult.Loading())
    val versionCode = _versionCode.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        fetchAuras()
        getMinimumVersion()
    }

    fun sendAura(aura: Long) = viewModelScope.launch {
        adminRepo.postAuras(aura)
        fetchAuras()
    }

    fun getMinimumVersion() = viewModelScope.launch {
        adminRepo.getMinimumVersion().collect {
            _versionCode.value = it
        }
    }

    fun postMinimumVersion(version: Int) = viewModelScope.launch {
        adminRepo.postMinimumVersion(version)
    }

    private fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAurasDecrypt(currentUserId()).collect {
            Log.d("auraDecrypted VM", it.toString())
            _totalAuras.value = it
        }
    }
}