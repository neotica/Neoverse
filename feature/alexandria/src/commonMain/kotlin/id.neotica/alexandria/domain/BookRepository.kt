package id.neotica.alexandria.domain

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import id.neotica.domain.model.alexandria.book.BookRequest
import id.neotica.domain.pagination.Pagination
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun getBooks(): Flow<ApiResult<Pagination<Book>>>
    fun getBook(bookId: String): Flow<ApiResult<Book>>
    fun addBook(book: BookRequest): Flow<ApiResult<Book>>
    fun editBook(id: String, book: BookRequest): Flow<ApiResult<Book>>
    fun deleteBook(bookId: String): Flow<ApiResult<String>>
    fun getUserBooks(username: String): Flow<ApiResult<List<Book>>>
}