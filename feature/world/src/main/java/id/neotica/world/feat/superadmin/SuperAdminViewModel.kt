package id.neotica.world.feat.superadmin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SuperAdminViewModel(
    private val gameRepo: AuraRepository,
    private val getIdUseCase: GetUserIdUseCase,
    private val firebase: FirebaseRepository
): ViewModel() {

    private val _totalAuras: MutableStateFlow<ApiResult<Long?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private fun currentUserId(): String = getIdUseCase.invoke()

    fun isLoggedIn(): Boolean {
        return getIdUseCase.invoke() != ""
    }

    init {
        fetchAuras()
    }

    fun sendAura(aura: Long) = viewModelScope.launch {
        gameRepo.postAuras(aura, currentUserId())
        fetchAuras()
    }

    private fun fetchAuras() = viewModelScope.launch {
        gameRepo.getAurasDecrypt(currentUserId()).collect {
            Log.d("auraDecrypted VM", it.toString())
            _totalAuras.value = it
        }
    }
}