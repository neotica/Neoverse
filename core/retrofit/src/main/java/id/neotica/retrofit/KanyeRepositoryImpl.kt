package id.neotica.retrofit

import android.util.Log
import id.neotica.domain.ApiResult
import id.neotica.domain.model.kanye.KanyeQuotes
import id.neotica.domain.remote.KanyeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class KanyeRepositoryImpl(
    private val apiService: ApiService
): KanyeRepository {
    override fun getQuote(): Flow<ApiResult<KanyeQuotes>> {
        return flow {
            try {
                val response = apiService.getQuote()
                Log.d("kanyerepo", response.quote)
                emit(ApiResult.Success(response))
            } catch (e: Exception) {
                emit(ApiResult.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }
}