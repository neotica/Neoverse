package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface KanyeRepository {
    fun getQuote(): Flow<ApiResult<id.neotica.domain.model.kanye.KanyeQuotes>>
}