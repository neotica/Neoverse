package id.neotica.alexandria.data.admin

import id.neotica.alexandria.domain.admin.AlexandriaAdminRepository
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.admin.WarningRequest
import id.neotica.domain.model.admin.WarningResponse
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.ktor.utils.safeApiCall
import id.neotica.neoverse.utils.baseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlexandriaAdminRepositoryImpl(
    private val client: HttpClient,
    private val authRepo: AuthRepository,
    private val token: TokenProvider? = null
): AlexandriaAdminRepository {
    override fun getBooks(): Flow<ApiResult<Pagination<Book>>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            authRepo.getCurrentUserToken()
//            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        client.get("${baseUrl}/admin/books"){
            headers {
                token.let { append("Authorization", "Bearer $it") }
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