package id.neotica.world.feat.market.merchant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import id.neotica.domain.model.market.ItemPrice
import id.neotica.domain.model.market.OwnerInfo
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MerchantRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.domain.utils.randomUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MerchantViewModel(
    private val auraRepo: AuraRepository,
    private val merchantRepo: MerchantRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
): ViewModel() {
    private val _totalAuras: MutableStateFlow<ApiResult<Games?>> = MutableStateFlow(ApiResult.Loading())
    val totalAuras = _totalAuras.asStateFlow()

    private val _merchant: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val merchant = _merchant.asStateFlow()

    private val _uploadState: MutableStateFlow<ApiResult<String>> = MutableStateFlow(ApiResult.Loading())
    val uploadState = _uploadState.asStateFlow()


    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    private val random = randomUUID()

    init {
        fetchAuras()
    }

    private fun fetchAuras() = viewModelScope.launch {
        auraRepo.getAuras(currentUserId()).collect {
            _totalAuras.value = it
        }
    }

    fun addHero(heroInput: Hero) = viewModelScope.launch {
        _merchant.value = ApiResult.Loading()
        val actualItem = heroInput.copy(
            id = random,
            owner = OwnerInfo(
                id = currentUserId(),
                name = currentUsername()
            ),
            imageUrl = merchantRepo.getHeroPicture("${heroInput.name}-$random")
        )
        val send = merchantRepo.addHero(actualItem)
        _merchant.value = send
    }

    fun uploadHeroPic(name: String, uri: String) = viewModelScope.launch {
        merchantRepo.uploadHeroPicture("$name-$random", uri).collect {
            _uploadState.value = it
        }
    }

    fun addItem(itemInput: Item) = viewModelScope.launch {
        _merchant.value = ApiResult.Loading()
        val dummyPrice = ItemPrice(
            aura = 0,
            coal = 100
        )
        val dummyItem = Item(
            id = random,
            name = itemInput.name,
            price = dummyPrice,
            power = itemInput.power,
            imageUrl = merchantRepo.getHeroPicture("${itemInput.name}-$random")
        )
        val send = merchantRepo.addItem(dummyItem)
        _merchant.value = send
    }
}