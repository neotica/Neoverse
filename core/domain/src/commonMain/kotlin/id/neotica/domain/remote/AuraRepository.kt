package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.World
import kotlinx.coroutines.flow.Flow

interface AuraRepository {
    suspend fun postAuras(aura: Long, userId: String)
    suspend fun getAuras(userId: String): Flow<ApiResult<World?>>
    suspend fun postAurasToBank(aura: Long, userId: String)
    suspend fun getBankAura(userId: String): Flow<ApiResult<World?>>
    suspend fun getAurasDecrypt(userId: String): Flow<ApiResult<Long?>>
}