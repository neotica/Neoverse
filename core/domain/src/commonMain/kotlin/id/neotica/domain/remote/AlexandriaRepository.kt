package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.legacy.BloodyAgeChapters
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.Chapter
import id.neotica.domain.model.alexandria.ChapterContent
import id.neotica.domain.model.alexandria.legacy.ChapterDetail
import id.neotica.domain.model.alexandria.legacy.Spirits
import id.neotica.domain.pagination.Pagination
import kotlinx.coroutines.flow.Flow

interface AlexandriaRepository {
    fun getSpirits(): Flow<ApiResult<List<Spirits>>>
    fun getBloodyAgeOld(): Flow<ApiResult<List<BloodyAgeChapters>>>
    fun getBloodyAgeChapter(chapter: String): Flow<ApiResult<List<ChapterDetail>>>
    fun getChaptersOld(url: String): Flow<ApiResult<BloodyAgeChapters>>
    fun getBooks(): Flow<ApiResult<Pagination<Book>>>
    fun editBook(book: Book): Flow<ApiResult<Book>>
    fun getUserBooks(username: String): Flow<ApiResult<List<Book>>>
    fun getChapters(bookId: String): Flow<ApiResult<List<Chapter>>>
    fun getBook(bookId: String): Flow<ApiResult<Book>>
    fun getContents(chapterId: Int): Flow<ApiResult<List<ChapterContent>>>
}