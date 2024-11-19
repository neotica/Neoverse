package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import kotlinx.coroutines.flow.Flow

interface AuraRepository {
    suspend fun postAuras(aura: Long, userId: String)
    suspend fun getAuras(userId: String): Flow<ApiResult<Games?>>
    suspend fun postAurasToBank(aura: Long, userId: String)
    suspend fun getBankAura(userId: String): Flow<ApiResult<Games?>>
}