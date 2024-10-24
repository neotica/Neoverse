package id.neotica.world.feat.market.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val auraRepo: AuraRepository,
    private val marketRepo: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val args = savedStateHandle.toRoute<Screen.ItemDetailScreen>()

    private val _hero: MutableStateFlow<ApiResult<Hero>> = MutableStateFlow(ApiResult.Loading())
    val itemModel = _hero.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()

    init {
        fetchAuras()
        getDetail()
    }

    private fun fetchAuras() = viewModelScope.launch {
        val auraFromRepo = auraRepo.getAuras(getUserIdUseCase.invoke())
        _totalAuras.value = auraFromRepo
    }

    private fun getDetail() = viewModelScope.launch {
        val get = marketRepo.getItem(args.id)
        _hero.value = get
    }

    fun sendAura(aura: Int) = viewModelScope.launch {
        auraRepo.postAuras(aura, currentUserId())
        fetchAuras()
    }

}