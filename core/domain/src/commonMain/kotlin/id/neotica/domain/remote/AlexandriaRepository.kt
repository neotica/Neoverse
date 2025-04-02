package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.ContentRequest
import id.neotica.domain.model.alexandria.chapter.ChapterRequest
import id.neotica.domain.model.alexandria.legacy.BloodyAgeChapters
import id.neotica.domain.pagination.PaginatedContent
import id.neotica.domain.pagination.Pagination
import kotlinx.coroutines.flow.Flow

interface AlexandriaRepository {
    fun getChaptersOld(url: String): Flow<ApiResult<BloodyAgeChapters>>
    fun getBooks(): Flow<ApiResult<Pagination<Book>>>
    fun editBook(book: Book): Flow<ApiResult<Book>>
    fun getUserBooks(username: String): Flow<ApiResult<List<Book>>>
    fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>>
    fun createChapter(chapter: ChapterRequest): Flow<ApiResult<Chapter>>
    fun getBook(bookId: String): Flow<ApiResult<Book>>
    fun getContents(chapterId: Int, page: Int? = null): Flow<ApiResult<PaginatedContent>>
    fun createContent(content: ContentRequest): Flow<ApiResult<ChapterContent>>
}