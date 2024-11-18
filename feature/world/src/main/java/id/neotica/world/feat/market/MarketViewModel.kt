package id.neotica.world.feat.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketViewModel(
    private val auraRepo: AuraRepository,
    private val marketRepo: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _hero: MutableStateFlow<ApiResult<List<Hero>>> = MutableStateFlow(ApiResult.Loading())
    val hero = _hero.asStateFlow()

    private val _item: MutableStateFlow<ApiResult<List<Item>>> = MutableStateFlow(ApiResult.Loading())
    val item = _item.asStateFlow()

    fun currentUserId(): String = getUserIdUseCase.invoke()

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
        marketRepo.getHeroes().collect {
            _hero.value = it
        }
    }

    fun fetchItems() = viewModelScope.launch {
        marketRepo.getItems().collect {
            _item.value = it
        }
    }
}