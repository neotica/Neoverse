package id.neotica.world.feat.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketViewModel(
    private val auraRepo: AuraRepository,
    private val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    fun fetchAuras() = viewModelScope.launch {
        val auraFromRepo = auraRepo.getAuras(getUserIdUseCase.invoke())
        _totalAuras.value = auraFromRepo
    }
}