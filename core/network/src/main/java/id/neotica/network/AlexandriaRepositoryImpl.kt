package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.BloodyAgeChapters
import id.neotica.domain.model.alexandria.Spirits
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.micro.ktorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlexandriaRepositoryImpl: AlexandriaRepository {

    override fun getSpirits(): Flow<ApiResult<List<Spirits>>> = flow {
        val result =  ktorClient
            .get("${baseUrl}/alexandria/goetia/spirits")
            .body<List<Spirits>>()
        emit(ApiResult.Success(result))
    }

    override fun getBloodyAge(): Flow<ApiResult<List<BloodyAgeChapters>>> = flow {
        val result = ktorClient
            .get("${baseUrl}/alexandria/bloody-age")
            .body<List<BloodyAgeChapters>>()
        emit(ApiResult.Success(result))
    }

    override fun getBloodyAgeChapter(chapter: String): Flow<ApiResult<String>> = flow {
        val result = ktorClient
            .get("${baseUrl}/alexandria/$chapter")
            .body<String>()
        emit(ApiResult.Success(result))
    }
}