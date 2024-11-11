package id.neotica.world.feat.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.InventoryRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val auraRepo: AuraRepository,
    private val inventoryRepo: InventoryRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _hero: MutableStateFlow<ApiResult<List<Hero>>> = MutableStateFlow(ApiResult.Loading())
    val itemModel = _hero.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()

    init {
        fetchAuras()
        fetchItem()
    }

    private fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAuras(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    fun fetchItem() = viewModelScope.launch {
        inventoryRepo.getHeroes(currentUserId()).collect {
            _hero.value = it
        }
    }
}