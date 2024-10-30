package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.market.Hero
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    suspend fun addItem(hero: Hero): ApiResult<String>
    suspend fun getItems(): Flow<ApiResult<List<Hero>>>
    suspend fun getItem(id: String): ApiResult<Hero>
    suspend fun uploadHeroPicture(heroId: String, uri: String): Flow<ApiResult<String>>
    suspend fun getHeroPicture(id: String): String
    suspend fun buyHero(hero: Hero, userId: String): ApiResult<String>
    suspend fun getUserHeroes(userId: String): Flow<ApiResult<List<Hero>>>
    suspend fun getUserHero(heroId: String, userId: String): ApiResult<Hero>
    suspend fun pawnHero(hero: Hero, userId: String): ApiResult<String>
}