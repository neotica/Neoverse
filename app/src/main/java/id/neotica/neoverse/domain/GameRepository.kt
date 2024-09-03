package id.neotica.neoverse.domain

import id.neotica.neoverse.data.model.Games
import id.neotica.neoverse.data.remote.ApiResult

interface GameRepository {
    suspend fun postAuras(aura: Int, userId: String)
    suspend fun getAuras(userId: String): ApiResult<Games?>
}