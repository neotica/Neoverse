package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.BloodyAgeChapters
import id.neotica.domain.model.alexandria.Books
import id.neotica.domain.model.alexandria.ChapterDetail
import id.neotica.domain.model.alexandria.Spirits
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.micro.ktorClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
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

    override fun getChapters(url: String): Flow<ApiResult<BloodyAgeChapters>> = flow {
        val result = ktorClient
            .get("${baseUrl}/alexandria/$url")
            .body<BloodyAgeChapters>()
        emit(ApiResult.Success(result))
    }

    override fun getBooks(): Flow<ApiResult<List<Books>>> = flow {
        val result = ktorClient
            .get("${baseUrl}/alexandria")
            .body<List<Books>>()
        emit(ApiResult.Success(result))
    }

    override fun getBloodyAgeChapter(chapter: String): Flow<ApiResult<List<ChapterDetail>>> = flow {
        emit(ApiResult.Loading())
        try {
            val result = ktorClient
                .get("${baseUrl}/alexandria/$chapter")
                .body<List<ChapterDetail>>()
            emit(ApiResult.Success(result))
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                emit(ApiResult.Error("Chapter not found (404)"))
            } else {
                emit(ApiResult.Error("Request failed: ${e.response.status}"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}