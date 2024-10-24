package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    suspend fun addItem(hero: Hero): ApiResult<String>
    suspend fun getItems(): Flow<ApiResult<List<Hero>>>
    suspend fun getItem(id: String): ApiResult<Hero>
}