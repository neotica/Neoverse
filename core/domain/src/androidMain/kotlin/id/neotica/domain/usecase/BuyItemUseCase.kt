package id.neotica.domain.usecase

import id.neotica.domain.model.market.Item
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.InventoryRepository
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BuyItemUseCase(
    private val auraRepo: AuraRepository,
    private val marketRepository: MarketRepository,
    private val inventoryRepository: InventoryRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
) {
    suspend operator fun invoke(item: Item) = main(item)

    private fun currentUserId(): String = getUserIdUseCase.invoke()

    private suspend fun main(itemParent: Item) = withContext(Dispatchers.IO) {
        val aura = itemParent.price.aura

        auraRepo.getAuras(currentUserId()).collect { currentUserAura ->
            auraRepo.getAuras(currentUserId()).collect { sellerAura ->
                inventoryRepository.getItem(currentUserId(), itemParent.id).collect { userItem ->
                    val heroValue = itemParent.copy()
                    val totalBuyer = currentUserAura.data?.aura?.minus(aura)
                    val totalSeller = sellerAura.data?.aura?.plus(aura)
                    val itemQty = userItem.data?.stock?.plus(1)
                    if (totalBuyer != null && totalSeller != null) {
                        currentUserId().let {
                            if (itemQty != null) {
                                val heroQty = heroValue.copy(stock = itemQty)
                                auraRepo.postAuras(totalBuyer, currentUserId())
                                marketRepository.buyItem(heroQty, currentUserId())
                            } else {
                                val heroQty = heroValue.copy(stock = 1)
                                auraRepo.postAuras(totalBuyer, currentUserId())
                                marketRepository.buyItem(heroQty, currentUserId())
                            }
                        }
                    }
                }
            }
        }
    }
}