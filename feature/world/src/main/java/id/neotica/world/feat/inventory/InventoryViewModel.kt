package id.neotica.world.feat.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
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
    val hero = _hero.asStateFlow()

    private val _items: MutableStateFlow<ApiResult<List<Item>>> = MutableStateFlow(ApiResult.Loading())
    val items = _items.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()

    init {
        fetchAuras()
        fetchHeroes()
        fetchItems()
    }

    private fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAuras(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    fun fetchHeroes() = viewModelScope.launch {
        inventoryRepo.getHeroes(currentUserId()).collect {
            _hero.value = it
        }
    }

    fun fetchItems() = viewModelScope.launch {
        inventoryRepo.getItems(currentUserId()).collect {
            _items.value = it
        }
    }
}