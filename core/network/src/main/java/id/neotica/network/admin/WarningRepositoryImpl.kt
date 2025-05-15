package id.neotica.network.admin

import id.neotica.domain.ApiResult
import id.neotica.domain.model.admin.WarningRequest
import id.neotica.domain.model.admin.WarningResponse
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.admin.WarningRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.utils.safeApiCall
import id.neotica.network.utils.safeApiCallNew
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class WarningRepositoryImpl(
    private val client: HttpClient,
    private val token: TokenProvider? = null
): WarningRepository {
    override fun getWarnings(): Flow<ApiResult<Pagination<WarningResponse>>> = safeApiCall {
        client.get("$baseUrl/admin/warning") {  }.body<Pagination<WarningResponse>>()
    }

    override fun newWarning(
        bookId: String,
        request: WarningRequest
    ): Flow<ApiResult<WarningResponse>> = safeApiCallNew<WarningResponse> {
        val tokenResult = token?.getToken()
        client.post("$baseUrl/admin/warning") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
            setBody(request)
        }
    }

    override fun updateWarning(
        bookId: String,
        request: WarningRequest
    ): Flow<ApiResult<WarningResponse>> = safeApiCall {
        val tokenResult = token?.getToken()
        client.put("$baseUrl/admin/warning/$bookId") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
            setBody(request)
        }.body<WarningResponse>()
    }

    override fun deleteWarning(id: String): Flow<ApiResult<String>> = safeApiCallNew<String> {
        val tokenResult = token?.getToken()
        client.delete("$baseUrl/admin/warning/$id") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $tokenResult")
            }
        }//.body<String>()
    }
}