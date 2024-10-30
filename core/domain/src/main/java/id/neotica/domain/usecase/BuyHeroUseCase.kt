package id.neotica.domain.usecase

import id.neotica.domain.model.market.Hero
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BuyHeroUseCase(
    private val auraRepo: AuraRepository,
    private val marketRepository: MarketRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
) {
    private fun currentUserId(): String = getUserIdUseCase.invoke()
    private fun currentUsername(): String = getUserNameUseCase.invoke()

    suspend fun main(heroParent: Hero) = withContext(Dispatchers.IO) {
        val aura = heroParent.price

        marketRepository.getHero(heroParent.id).collect { hero ->
            auraRepo.getAurasFlow(currentUserId()).collect { currentUserAura ->
                auraRepo.getAurasFlow(hero.data?.owner?.id.toString()).collect { sellerAura ->
                    auraRepo.getBankAura(currentUserId()).collect { bankAura ->
                        val heroValue = heroParent.copy(
                            owner = heroParent.owner.copy(
                                id = currentUserId(),
                                name = currentUsername()
                            )
                        )
                        val totalBuyer = currentUserAura.data?.aura?.minus(aura)
                        val totalSeller = sellerAura.data?.aura?.plus(aura)
                        val totalBank = bankAura.data?.aura?.minus(aura)


                        if (totalBuyer != null && totalSeller != null && totalBank != null) {
                            hero.data?.owner?.id?.let {
                                auraRepo.postAuras(totalSeller, it)
                                auraRepo.postAuras(totalBuyer, currentUserId())
                                auraRepo.postAurasToBank(totalBank, currentUserId())
                                marketRepository.buyHero(heroValue, currentUserId())
                            }
                        }
                    }
                }
            }
        }
    }
}