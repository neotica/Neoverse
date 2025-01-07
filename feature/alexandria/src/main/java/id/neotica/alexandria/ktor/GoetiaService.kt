package id.neotica.alexandria.ktor

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Spirits
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoetiaService {

    fun getSpirits(): Flow<ApiResult<List<Spirits>>> = flow {
        val result =  httpClient
            .get("https://api.neotica.id/alexandria/goetia/spirits")
            .body<List<Spirits>>()
        emit(ApiResult.Success(result))
    }
}