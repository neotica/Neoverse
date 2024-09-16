package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games

interface GameRepository {
    suspend fun postAuras(aura: Int, userId: String)
    suspend fun getAuras(userId: String): ApiResult<Games?>
}