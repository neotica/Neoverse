package id.neotica.network

import com.google.firebase.auth.FirebaseAuth
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.model.alexandria.legacy.BloodyAgeChapters
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.micro.ktorClient
import id.neotica.network.utils.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AlexandriaRepositoryImpl: AlexandriaRepository {

    override fun getBooks(): Flow<ApiResult<Pagination<Book>>> = safeApiCall {
        ktorClient.get("${baseUrl}/alexandria"){
        }.body<Pagination<Book>>()
    }

    override fun getUserBooks(username: String): Flow<ApiResult<List<Book>>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.get("${baseUrl}/alexandria/book"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
            url { parameters.append("author", username) }
        }.body<List<Book>>()
    }

    override fun getBook(bookId: String): Flow<ApiResult<Book>> = safeApiCall {
        ktorClient.get("${baseUrl}/alexandria/$bookId").body<Book>()
    }

    override fun editBook(book: Book): Flow<ApiResult<Book>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.put("${baseUrl}/alexandria/${book.id}"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
                append("Content-Type", "application/json")
            }
            setBody(book)
        }.body<Book>()
    }

    override fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>> = safeApiCall {
        ktorClient.get("${baseUrl}/alexandria/$bookId").body<List<Chapter>>()
    }

    override fun createChapter(chapter: ChapterRequest): Flow<ApiResult<Chapter>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.post("${baseUrl}/alexandria/chapters") {
            headers {
                append("Content-Type", "application/json")
                token?.let { append("Authorization", "Bearer $it") }
            }
            setBody(chapter)
        }.body()
    }

    override fun deleteChapter(chapterId: Int): Flow<ApiResult<String>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.delete("${baseUrl}/alexandria/chapters/$chapterId") {
            headers {
                append("Content-Type", "application/json")
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<String>()
    }

    override fun getContents(chapterId: Int, page: Int?): Flow<ApiResult<PaginatedContent>> = safeApiCall {
        ktorClient.get("${baseUrl}/alexandria/chapters/contents/$chapterId"){
            url {
                parameters.append("page", page?.toString() ?: "1")
            }
        }.body<PaginatedContent>()
    }

    override fun getChaptersOld(url: String): Flow<ApiResult<BloodyAgeChapters>> = flow {
        try {
            val result = ktorClient
                .get("${baseUrl}/alexandria-old/$url")
                .body<BloodyAgeChapters>()
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

    override fun createContent(content: ContentRequest): Flow<ApiResult<ChapterContent>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.post("${baseUrl}/alexandria/chapters/contents") {
            headers {
                append("Content-Type", "application/json")
                token?.let { append("Authorization", "Bearer $it") }
            }
            setBody(content)
        }.body<ChapterContent>()
    }

    override fun editContent(content: NewContentRequest, id: Int, page: Int): Flow<ApiResult<String>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.put("${baseUrl}/alexandria/chapters/contents/${id}?page=$page") {
            headers {
                append("Content-Type", "application/json")
                token?.let { append("Authorization", "Bearer $it") }
            }
            setBody(content)
        }.body()
    }

    override fun deleteContent(contentId: Int): Flow<ApiResult<String>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }

        ktorClient.delete("${baseUrl}/alexandria/chapters/contents/$contentId") {
            headers {
                append("Content-Type", "application/json")
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<String>()
    }

}