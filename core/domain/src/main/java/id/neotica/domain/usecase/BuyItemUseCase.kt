package id.neotica.domain.usecase

import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BuyItemUseCase(
    private val auraRepo: AuraRepository,
    private val marketRepository: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
) {
    suspend operator fun invoke(item: Item) = main(item)

    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    private suspend fun main(itemParent: Item) = withContext(Dispatchers.IO) {
        val aura = itemParent.price.aura
        auraRepo.getAuras(currentUserId()).collect { currentUserAura ->
            auraRepo.getAuras(currentUserId()).collect { sellerAura ->
                val heroValue = itemParent.copy()
                val totalBuyer = currentUserAura.data?.aura?.minus(aura)
                val totalSeller = sellerAura.data?.aura?.plus(aura)

                if (totalBuyer != null && totalSeller != null) {
                    currentUserId().let {
                        auraRepo.postAuras(totalBuyer, currentUserId())
                        marketRepository.buyItem(heroValue, currentUserId())
                    }
                }
            }
        }
    }
}