package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    suspend fun getItems(): Flow<ApiResult<List<Hero>>>
    suspend fun getHero(id: String): Flow<ApiResult<Hero>>
    suspend fun getUserHero(heroId: String, userId: String): Flow<ApiResult<Hero>>
    suspend fun buyHero(hero: Hero, userId: String): ApiResult<String>
    suspend fun pawnHero(hero: Hero, userId: String): ApiResult<String>
}