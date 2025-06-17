package id.neotica.alexandria.alexandria

import id.neotica.alexandria.client.createMockClient
import id.neotica.alexandria.data.BookRepositoryImpl
import id.neotica.alexandria.domain.BookRepository
import id.neotica.alexandria.dummy.FakeCurrentUserTokenImpl
import id.neotica.auth.data.AuthRepositoryImpl
import id.neotica.auth.domain.AuthRepository
import id.neotica.auth.domain.GetCurrentUserTokenUseCase
import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.Book
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class BookRepositoryImplTest {

    private lateinit var client: HttpClient
    private lateinit var repository: BookRepository
    private lateinit var currentUserTokenUseCase: GetCurrentUserTokenUseCase
    private lateinit var authRepo: AuthRepository
    val dummy = BookDummy()

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `emits Loading then Success when getBooks is called`() = runTest {
        // Given
        client = createMockClient(content = dummy.emptyBookResponseJson)
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)
        authRepo = AuthRepositoryImpl(
            auth = TODO()
        )

        // When
        val emissions = repository.getBooks()

        // Then
        assertTrue(emissions.first() is ApiResult.Loading)
    }

    @Test
    fun `GetBooks, Successful API call with valid book data`() = runBlocking {
        // Given
        val expectedBook = listOf<Book>(dummy.bookResponse)
        client = createMockClient(content = dummy.paginatedBookResponseJson)
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)
        // When
        val result = repository.getBooks().last()
        println(result.data)
        // Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedBook, successData.content)
        assertEquals(1, successData.currentPage)
    }

    @Test
    fun `GetBooks, Successful API call with empty book list`() = runBlocking {
        // Given
        val expectedBook = emptyList<Book>()
        client = createMockClient(content = dummy.emptyBookResponseJson)
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)
        // When
        val result = repository.getBooks().last()
        println("empty: " + result.data)
        // Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedBook, successData.content)
//        assertEquals(expectedBook, successData.content[0])
        assertEquals(0, successData.currentPage)
    }

    @Test
    fun `getBook by author`() = runBlocking {
        //Given
        val expectedResponse = dummy.booksByAuthorResponse
        val username = "tarakh"
        client = createMockClient(
            url = "/alexandria/book",
            content = dummy.booksByAuthorResponseJson,
            expectedParams = mapOf("author" to username)
        )
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)
        //When
        val result = repository.getUserBooks(username).last()
        println("💀: " + result.errorMessage)
        //Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedResponse, successData)
    }

    @Test
    fun `getBook by author returns error when not found`() = runBlocking {
        // Given
        val username = "tarakh"
        val expectedErrorMessage = "404 Not Found"

        client = createMockClient(
            status = HttpStatusCode.NotFound, // simulate not found
            url = "/alexandria/book",
            expectedParams = mapOf("author" to username),
            content = dummy.booksByAuthorNotFoundJson
        )
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)

        // When
        val result = repository.getUserBooks(username).last()

        // Then
        assertTrue(result is ApiResult.Error)
        val error = result as ApiResult.Error
        println("💀: ${error.errorMessage}")
        assertTrue(error.errorMessage == expectedErrorMessage)
    }

    @Test
    fun `getBook by id`() = runBlocking {
        //Given
        val expectedResponse = dummy.bookByIdResponse
        val id = expectedResponse.id
        client = createMockClient(
            url = "/alexandria/$id",
            content = dummy.bookByIdJson,
        )
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)
        //When
        val result = repository.getBook(id).last()
        println("getBookById: " + result.data)
        //Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedResponse, successData)
    }

    @Test
    fun `API call with server error  e g   500 `() {
        // Verify that getBooks() emits ApiResult.Error when the API returns a server error status code.
        // TODO implement test
    }

    @Test
    fun `API call with client error  e g   400 `() {
        // Verify that getBooks() emits ApiResult.Error when the API returns a client error status code.
        // TODO implement test
    }

    @Test
    fun `Network connectivity issue`() {
        // Verify that getBooks() emits ApiResult.Error when there is a network connectivity issue preventing the API call from completing.
        // TODO implement test
    }

    @Test
    fun `Malformed response body`() {
        // Verify that getBooks() handles a malformed or unexpected response body from the API and emits ApiResult.Error.
        // TODO implement test
    }

    @Test
    fun `Empty response body for successful call`() {
        // Verify that getBooks() handles a scenario where the API returns a successful status code but an empty response body, and emits ApiResult.Error.
        // TODO implement test
    }

    @Test
    fun `Pagination metadata correctness`() {
        // If the API supports pagination metadata (like total items, current page, etc.), verify that these fields in the Pagination object are correctly parsed and emitted.
        // TODO implement test
    }

    @Test
    fun `Headers are not sent for this specific endpoint`() {
        // Verify that no custom headers (like Authorization) are being sent with the GET request for getBooks(), as per the provided code snippet.
        // TODO implement test
    }

    @Test
    fun `URL construction correctness`() {
        // Verify that the URL for the API call is correctly constructed using the baseUrl and the specific endpoint '/alexandria'.
        // TODO implement test
    }

    @Test
    fun `HttpClient exceptions during call`() {
        // Verify that getBooks() correctly handles and emits ApiResult.Error for exceptions thrown by the HttpClient during the API call process (e.g., `HttpRequestTimeoutException`).
        // TODO implement test
    }

    @Test
    fun `Flow cancellation handling`() {
        // Verify that the Flow emitted by getBooks() can be cancelled correctly and that the underlying HttpClient request is also cancelled.
        // TODO implement test
    }

    @Test
    fun `addBook emits loading then success on VALID POST`() = runTest {
        val bookRequest = dummy.addBookRequest
        val expectedBook = dummy.bookResponse
        val jsonResponse = dummy.addBookResponseJson

        client = createMockClient(
            method = "POST",
            url = "/alexandria",
            content = jsonResponse,
            contentType = "application/json"
        )

        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)

        //When
        val emission = repository.addBook(bookRequest).toList()

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission: "+emission.last().data .toString())
        val resultBook = (emission[1] as ApiResult.Success).data
        assertEquals(expectedBook, resultBook)
    }

    @Test
    fun `editBook succeed`() = runTest {
        val id = dummy.bookResponse.id
        val bookRequest = dummy.editBookRequest
        val expectedBook = dummy.editBookResponse
        val jsonResponse = dummy.editBookResponseJson

        client = createMockClient(
            method = "PUT",
            url = "/alexandria/$id",
            content = jsonResponse,
            contentType = "application/json"
        )

        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)

        //When
        val emission = repository.editBook(id, bookRequest).toList()

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission put: " + emission.last().data.toString())

        val resultBook = (emission[1] as ApiResult.Success).data
        assertEquals(expectedBook, resultBook)
    }

    @Test
    fun `deleteBook succeed`() = runTest {
        val id = dummy.bookResponse.id
        val jsonResponse = dummy.deleteBookResponseJson

        client = createMockClient(
            method = "DELETE",
            url = "/alexandria/$id",
            content = jsonResponse,
        )

        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = BookRepositoryImpl(client, currentUserTokenUseCase)

        //When
        val emission = repository.deleteBook(id).toList()

        //Then
        assertTrue(emission.last() is ApiResult.Success)

        val result = (emission[1] as ApiResult.Success).data
        assertEquals(jsonResponse, result)
    }
}