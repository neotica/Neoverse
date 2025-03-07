package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Spirits
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.network.micro.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlexandriaRepositoryImpl: AlexandriaRepository {

    override fun getSpirits(): Flow<ApiResult<List<Spirits>>> = flow {
        val result =  ktorClient
            .get("https://api.neotica.id/alexandria/goetia/spirits")
            .body<List<Spirits>>()
        emit(ApiResult.Success(result))
    }
}