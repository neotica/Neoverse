package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.kanye.KanyeQuotes
import kotlinx.coroutines.flow.Flow

interface KanyeRepository {
    fun getQuote(): Flow<ApiResult<KanyeQuotes>>
}