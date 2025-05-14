package id.neotica.network.alexandria

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class ChapterRepositoryImpl(
    private val client: HttpClient,
    private val token: TokenProvider
): ChapterRepository {
    override fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>> = safeApiCall {
        client.get("${baseUrl}/alexandria/chapters").body<List<Chapter>>()
    }

    override fun createChapter(chapter: ChapterRequest): Flow<ApiResult<Chapter>> = safeApiCall {
        client.post("${baseUrl}/alexandria/chapters") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
            setBody(chapter)
        }.body()
    }

    override fun deleteChapter(chapterId: Int): Flow<ApiResult<String>> = safeApiCall {
        client.delete("${baseUrl}/alexandria/chapters/$chapterId") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
        }.body<String>()
    }
}