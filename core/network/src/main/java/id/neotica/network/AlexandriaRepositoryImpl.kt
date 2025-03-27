package id.neotica.network

import com.google.firebase.auth.FirebaseAuth
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.legacy.BloodyAgeChapters
import id.neotica.domain.model.alexandria.legacy.ChapterDetail
import id.neotica.domain.model.alexandria.legacy.Spirits
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.micro.ktorClient
import id.neotica.network.utils.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AlexandriaRepositoryImpl: AlexandriaRepository {

    override fun getBooks(): Flow<ApiResult<List<Book>>> = safeApiCall {
        ktorClient.get("${baseUrl}/alexandria"){
        }.body<List<Book>>()
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
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.get("${baseUrl}/alexandria/$bookId"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<Book>()
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
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.get("${baseUrl}/alexandria/$bookId"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<List<Chapter>>()
    }

    override fun getContents(chapterId: Int): Flow<ApiResult<List<ChapterContent>>> = safeApiCall {
        val token = withContext(Dispatchers.IO) {
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        }
        ktorClient.get("${baseUrl}/alexandria/chapters/contents/filter/$chapterId"){
            headers {
                token?.let { append("Authorization", "Bearer $it") }
            }
        }.body<List<ChapterContent>>()
    }

    override fun getSpirits(): Flow<ApiResult<List<Spirits>>> = flow {
        val result =  ktorClient
            .get("${baseUrl}/alexandria-old/goetia/spirits")
            .body<List<Spirits>>()
        emit(ApiResult.Success(result))
    }

    override fun getBloodyAgeOld(): Flow<ApiResult<List<BloodyAgeChapters>>> = flow {
        val result = ktorClient
            .get("${baseUrl}/alexandria-old/bloody-age-legacy")
            .body<List<BloodyAgeChapters>>()
        emit(ApiResult.Success(result))
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


    override fun getBloodyAgeChapter(chapter: String): Flow<ApiResult<List<ChapterDetail>>> = flow {
        emit(ApiResult.Loading())
        try {
            val result = ktorClient
                .get("${baseUrl}/alexandria-old/$chapter")
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