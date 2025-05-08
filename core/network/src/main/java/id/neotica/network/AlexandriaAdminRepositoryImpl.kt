package id.neotica.network

import com.google.firebase.auth.FirebaseAuth
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.admin.WarningRequest
import id.neotica.domain.model.alexandria.admin.WarningResponse
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.AlexandriaAdminRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AlexandriaAdminRepositoryImpl(
    private val client: HttpClient,
    private val token: TokenProvider? = null
): AlexandriaAdminRepository {
    override fun getBooks(): Flow<ApiResult<Pagination<Book>>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        client.get("${baseUrl}/admin/books"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<Pagination<Book>>()
    }

    override fun getWarnings(): Flow<ApiResult<Pagination<WarningResponse>>> = safeApiCall {
        client.get("${baseUrl}/admin/warning"){}.body<Pagination<WarningResponse>>()
    }

    override fun updateWarning(
        id: String,
        warningRequest: WarningRequest
    ): Flow<ApiResult<WarningResponse>> = safeApiCall {
        println("🚀 About to send PUT to: ${baseUrl}/admin/warning/$id")
        println("🚀 With body: isAppealed=${warningRequest.isAppealed}, isApproved=${warningRequest.isApproved}")

        client.put("${baseUrl}/admin/warning/$id") {
            contentType(ContentType.Application.Json)
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
            setBody(warningRequest)
        }.body<WarningResponse>()
    }
}