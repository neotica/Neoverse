package id.neotica.world.feat.mining

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MiningViewModel(
    getUserIdUseCase: GetUserIdUseCase,
    private val auraRepo: AuraRepository
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val currentUserId: String = getUserIdUseCase.invoke()

    init {
        fetchAuras()
    }

    private fun fetchAuras() = viewModelScope.launch {
        _totalAuras.value = ApiResult.Loading()
        auraRepo.getAuras(currentUserId).collect {
            _totalAuras.value = it
        }
    }

    fun postAura(aura: Int) = viewModelScope.launch {
        val total = _totalAuras.value.data?.aura
        if (total != null) {
            auraRepo.postAuras(total.toInt() + aura, currentUserId)
        }
        fetchAuras()
    }
}