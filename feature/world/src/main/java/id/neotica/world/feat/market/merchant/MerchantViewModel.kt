package id.neotica.world.feat.market.merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.HeroBackground
import id.neotica.domain.model.market.OwnerInfo
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MerchantViewModel(
    private val auraRepo: AuraRepository,
    private val marketRepo: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _hero: MutableStateFlow<ApiResult<List<Hero>>> = MutableStateFlow(ApiResult.Loading())
    val hero = _hero.asStateFlow()

    private val _merchant: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val merchant = _merchant.asStateFlow()

    private val _uploadState: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val uploadState = _uploadState.asStateFlow()


    private fun currentUserId(): String = getUserIdUseCase.invoke()

    private val random = randomUUID()

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

    fun addItem(heroInput: Hero) = viewModelScope.launch {
        _merchant.value = ApiResult.Loading()
        val dummyItem = Hero(
            id = random,
            name = heroInput.name,
            price = heroInput.price,
            power = heroInput.power,
            owner = OwnerInfo(
                id = currentUserId(),
                name = "laetuz"
            ),
            background = HeroBackground(
                story = heroInput.background?.story,
                origin = heroInput.background?.origin,
                pantheon = heroInput.background?.pantheon
            ),
            imageUrl = marketRepo.getHeroPicture("${heroInput.name}-$random")
        )
        val send = marketRepo.addItem(dummyItem)
        _merchant.value = send
    }

    fun uploadHeroPic(name: String, uri: String) = viewModelScope.launch {
        marketRepo.uploadHeroPicture("$name-$random", uri).collect {
            _uploadState.value = it
        }
    }

    fun fetchItem() = viewModelScope.launch {
        marketRepo.getItems().collect {
            _hero.value = it
        }
    }
}