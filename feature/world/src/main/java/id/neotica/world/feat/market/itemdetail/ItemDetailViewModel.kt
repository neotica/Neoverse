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
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.state.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val auraRepo: AuraRepository,
    private val marketRepo: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()


    private val args = savedStateHandle.toRoute<WorldScreen.ItemDetailScreen>()

    private val _hero: MutableStateFlow<ApiResult<Hero>> = MutableStateFlow(ApiResult.Loading())
    val itemModel = _hero.asStateFlow()

    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    init {
        fetchAuras()
        when(screenState()) {
            ScreenState.Global.toString() -> getDetail()
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

    fun buy(aura: Int) = viewModelScope.launch {
        _hero.value.data?.let {
            val heroValue = it.copy(
                owner = it.owner.copy(
                    id = currentUserId(),
                    name = currentUsername()
                )
            )
            _totalAuras.value.data?.let { auraValue ->
                val total = auraValue.aura.minus(aura)
                auraRepo.postAuras(total, currentUserId())
                marketRepo.buyHero(hero = heroValue, currentUserId())
            }

        }
        fetchAuras()
    }

    fun pawn(aura: Int) = viewModelScope.launch {
        _hero.value.data?.let {
            val heroValue = it.copy(
                price = aura,
                owner = it.owner.copy(
                    id = currentUserId(),
                    name = currentUsername()
                )
            )
            _totalAuras.value.data?.let { auraValue ->
                val total = auraValue.aura.plus(aura)
                auraRepo.postAuras(total, currentUserId())
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
            auraRepo.getAurasFromBank(currentUserId())
            marketRepo.buyHero(hero = heroValue, currentUserId())
        }
        fetchAuras()
    }

    fun displayHero(aura: Int) = viewModelScope.launch {
        _hero.value.data?.let {
            val heroValue = it.copy(
                owner = it.owner.copy(
                    id = currentUserId(),
                    name = currentUsername()
                )
            )
            auraRepo.postAuras(aura, currentUserId())
            marketRepo.buyHero(hero = heroValue, currentUserId())
        }
        fetchAuras()
    }

    private fun getDetailUser() = viewModelScope.launch {
        val get = marketRepo.getUserHero(args.id, currentUserId())
        _hero.value = get
    }

}