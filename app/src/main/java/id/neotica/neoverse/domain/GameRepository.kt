package id.neotica.neoverse.domain

import id.neotica.neoverse.data.model.Games
import id.neotica.neoverse.data.remote.ApiResult
import kotlinx.coroutines.flow.Flow

interface GameRepository {
//    suspend fun getAuras(userId: String): Flow<ApiResult<Int>>
    suspend fun lostAuras(aura: Int, userId: String)
    suspend fun receiveAuras(aura: Int, userId: String)
    suspend fun getAuras(userId: String): ApiResult<Games?>
}