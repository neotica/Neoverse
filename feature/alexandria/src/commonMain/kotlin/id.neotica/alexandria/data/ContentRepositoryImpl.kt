package id.neotica.alexandria.data

import id.neotica.alexandria.domain.ContentRepository
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.ktor.utils.safeApiCall
import id.neotica.ktor.utils.safeApiCallNew
import id.neotica.neoverse.utils.baseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class ContentRepositoryImpl(
    private val client: HttpClient,
    private val authRepository: AuthRepository
): ContentRepository {
    override fun getContents(chapterId: Int, page: Int?): Flow<ApiResult<PaginatedContent>> = safeApiCallNew<PaginatedContent> {
        client.get("${baseUrl}/alexandria/chapters/contents/$chapterId"){
            url {
                parameters.append("page", page?.toString() ?: "1")
            }
        }
    }

    override fun createContent(content: ContentRequest): Flow<ApiResult<ChapterContent>> = safeApiCallNew<ChapterContent> {
        val tokenResult = authRepository.getCurrentUserToken()
        client.post("${baseUrl}/alexandria/chapters/contents") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
            setBody(content)
        }
    }

    override fun editContent(content: NewContentRequest, id: Int, page: Int): Flow<ApiResult<String>> = safeApiCall {
        val tokenResult = authRepository.getCurrentUserToken()
        client.put("${baseUrl}/alexandria/chapters/contents/${id}?page=$page") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
            setBody(content)
        }.body()
    }

    override fun deleteContent(contentId: Int): Flow<ApiResult<String>> = safeApiCall {
        val tokenResult = authRepository.getCurrentUserToken()
        client.delete("${baseUrl}/alexandria/chapters/contents/$contentId") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
        }.body<String>()
    }
}