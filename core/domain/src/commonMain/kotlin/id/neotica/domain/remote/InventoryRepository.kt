package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Item
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    suspend fun getHeroes(userId: String): Flow<ApiResult<List<id.neotica.domain.model.market.Hero>>>
    suspend fun getHero(heroId: String, userId: String): Flow<ApiResult<id.neotica.domain.model.market.Hero>>
    suspend fun getItems(userId: String): Flow<ApiResult<List<Item>>>
    suspend fun getItem(userId: String, itemId: String): Flow<ApiResult<Item>>
}