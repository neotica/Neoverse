package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.model.alexandria.content.NewContentRequest
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.pagination.Pagination
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.domain.remote.alexandria.ContentRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow

class AlexandriaRepositoryImpl(
    private val client: HttpClient,
    private val token: TokenProvider? = null
): BookRepository, ChapterRepository, ContentRepository {

    override fun getBooks(): Flow<ApiResult<Pagination<Book>>> = safeApiCall {
        client.get("${baseUrl}/alexandria"){
        }.body<Pagination<Book>>()
    }

    override fun addBook(book: BookRequest): Flow<ApiResult<Book>> = safeApiCall {
        client.post("$baseUrl/alexandria") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
            setBody(book)
        }.body<Book>()
    }

    override fun getUserBooks(username: String): Flow<ApiResult<List<Book>>> = safeApiCall {
        client.get("${baseUrl}/alexandria/book"){
            headers {
                append("Authorization", "Bearer $token")
            }
            url { parameters.append("author", username) }
        }.body<List<Book>>()
    }

    override fun getBook(bookId: String): Flow<ApiResult<Book>> = safeApiCall {
        client.get("${baseUrl}/alexandria/$bookId").body<Book>()
    }

    override fun editBook(book: Book): Flow<ApiResult<Book>> = safeApiCall {
        client.put("${baseUrl}/alexandria/${book.id}"){
            headers {
                append("Authorization", "Bearer $token")
                append("Content-Type", "application/json")
            }
            setBody(book)
        }.body<Book>()
    }

    override fun deleteBook(bookId: String): Flow<ApiResult<String>> = safeApiCall {
        client.delete("${baseUrl}/alexandria/$bookId") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
        }.body<String>()
    }

    override fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>> = safeApiCall {
        client.get("${baseUrl}/alexandria/$bookId").body<List<Chapter>>()
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

    override fun getContents(chapterId: Int, page: Int?): Flow<ApiResult<PaginatedContent>> = safeApiCall {
        client.get("${baseUrl}/alexandria/chapters/contents/$chapterId"){
            url {
                parameters.append("page", page?.toString() ?: "1")
            }
        }.body()
    }

    override fun createContent(content: ContentRequest): Flow<ApiResult<ChapterContent>> = safeApiCall {
        client.post("${baseUrl}/alexandria/chapters/contents") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
            setBody(content)
        }.body<ChapterContent>()
    }

    override fun editContent(content: NewContentRequest, id: Int, page: Int): Flow<ApiResult<String>> = safeApiCall {
        client.put("${baseUrl}/alexandria/chapters/contents/${id}?page=$page") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
            setBody(content)
        }.body()
    }

    override fun deleteContent(contentId: Int): Flow<ApiResult<String>> = safeApiCall {
        client.delete("${baseUrl}/alexandria/chapters/contents/$contentId") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $token")
            }
        }.body<String>()
    }

}