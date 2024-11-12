package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import kotlinx.coroutines.flow.Flow

interface MerchantRepository {
    suspend fun addHero(hero: Hero): ApiResult<String>
    suspend fun uploadHeroPicture(heroId: String, uri: String): Flow<ApiResult<String>>
    suspend fun getHeroPicture(id: String): String
    suspend fun addItem(item: Item): ApiResult<String>
}