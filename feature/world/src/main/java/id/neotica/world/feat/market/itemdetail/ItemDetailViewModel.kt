package id.neotica.world.feat.market.itemdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.usecase.BuyHeroUseCase
import id.neotica.domain.usecase.BuyItemUseCase
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.state.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val auraRepo: AuraRepository,
    private val marketRepo: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val buyHeroUseCase: BuyHeroUseCase,
    private val buyItemUseCase: BuyItemUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _sellerAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    private val _bankAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())

    private val args = savedStateHandle.toRoute<WorldScreen.ItemDetailScreen>()

    private val _hero: MutableStateFlow<ApiResult<Hero>> = MutableStateFlow(ApiResult.Loading())
    val hero = _hero.asStateFlow()

    private val _item: MutableStateFlow<ApiResult<Item>> = MutableStateFlow(ApiResult.Loading())
    val item = _item.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    init {
        fetchAuras()
        fetchBankAuras()
        when(screenState()) {
            ScreenState.Global.toString() -> {
                getDetail()
                getItemDetail()
            }
            ScreenState.UserInventory.toString() -> getDetailUser()
            ScreenState.User.toString() -> getDetail()
        }
    }

    fun screenState(): String = when (args.type) {
        ScreenState.Global.toString(),
        ScreenState.User.toString(),
        ScreenState.UserInventory.toString() -> args.type
        else -> ""
    }

    private fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAuras(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    private fun fetchBankAuras() = viewModelScope.launch {
        auraRepo.getBankAura(currentUserId()).collect {
            _bankAuras.value = it
        }
    }

    fun fetchSellerAuras(id: String) = viewModelScope.launch {
        auraRepo.getAuras(id).collect {
            _sellerAuras.value = it
        }
    }

    private fun getDetail() = viewModelScope.launch {
        marketRepo.getHero(args.id).collect {
            _hero.value = it
        }
    }

    private fun getItemDetail() = viewModelScope.launch {
        marketRepo.getItem(args.id).collect {
            _item.value = it
        }
    }

    fun buy(hero: Hero) = CoroutineScope(Dispatchers.IO).launch { buyHeroUseCase.invoke(hero) }

    fun buyItem(item: Item) = CoroutineScope(Dispatchers.IO).launch { buyItemUseCase.invoke(item) }

    fun pawn(aura: Int) = viewModelScope.launch {
        _hero.value.data?.let {
            val heroValue = it.copy(
                price = aura,
                owner = it.owner.copy(
                    id = currentUserId(),
                    name = currentUsername()
                )
            )
            _bankAuras.value.data?.let { auraValue ->
                val total = auraValue.aura.plus(aura)
                auraRepo.postAurasToBank(total, currentUserId())
                marketRepo.pawnHero(hero = heroValue, currentUserId())
            }
        }
        fetchAuras()
    }

    fun store(aura: Int) = viewModelScope.launch {
        _hero.value.data?.let {
            val heroValue = it.copy(
                owner = it.owner.copy(
                    id = currentUserId(),
                    name = currentUsername()
                )
            )
            _bankAuras.value.data?.let { auraValue ->
                val total = auraValue.aura.minus(aura)
                auraRepo.postAurasToBank(total, currentUserId())
                marketRepo.buyHero(hero = heroValue, currentUserId())
            }
        }
        fetchAuras()
    }

    private fun getDetailUser() = viewModelScope.launch {
        marketRepo.getUserHero(args.id, currentUserId()).collect {
            _hero.value = it
        }
    }
}