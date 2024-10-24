package id.neotica.world.feat.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.OwnerInfo
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.utils.randomUUID
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
    val itemModel = _hero.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()

    init {
        fetchAuras()
        fetchItem()
    }

    fun fetchAuras() = viewModelScope.launch {
        val auraFromRepo = auraRepo.getAuras(getUserIdUseCase.invoke())
        _totalAuras.value = auraFromRepo
    }

    fun sendAura(aura: Int) = viewModelScope.launch {
        auraRepo.postAuras(aura, currentUserId())
        fetchAuras()
    }

    fun addItem() = viewModelScope.launch {
        val dummyItem = Hero(
            id = randomUUID(),
            name = "test",
            price = 2000,
            power = 20,
            owner = OwnerInfo(
                id = currentUserId(),
                name = "laetuz"
            ),
            imageUrl = "https://firebasestorage.googleapis.com/v0/b/neoverse-neotica.appspot.com/o/profilePicture%2Ficon.png?alt=media&token=c6e5b358-78c6-41c1-9c0c-a314e4cd258c"
        )
        marketRepo.addItem(dummyItem)
    }

    fun fetchItem() = viewModelScope.launch {
        marketRepo.getItems().collect {
            _hero.value = it
        }
    }
}