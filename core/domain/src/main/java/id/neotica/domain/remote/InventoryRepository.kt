package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import id.neotica.domain.model.market.Item
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    suspend fun getHeroes(userId: String): Flow<ApiResult<List<Hero>>>
    suspend fun getItems(userId: String): Flow<ApiResult<List<Item>>>
}