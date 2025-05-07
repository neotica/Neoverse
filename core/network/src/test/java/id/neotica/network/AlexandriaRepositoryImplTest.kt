package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.network.alexandria.BookRepositoryImpl
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class AlexandriaRepositoryImplTest {

    private lateinit var repository: BookRepositoryImpl
    val fakeTokenProvider = FakeTokenProvider()

    private fun createMockClient(
        status: HttpStatusCode = HttpStatusCode.OK,
        content: String = "",
        contentType: String = "application/json"
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            assertEquals("GET", request.method.value)
            assertEquals("/alexandria", request.url.encodedPath)
            respond(
                content = content,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, contentType)
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `Successful retrieval of books`() = runBlocking {
        // Verify that the method returns a Flow emitting ApiResult.Success
        // containing a Pagination object with a list of books.
        val mockResponse = """
            {
                "content": [
                    {
                        "uuid": "1",
                        "title": "Test Book",
                        "author": "Author A",
                        "language": "EN",
                        "isPublished": true,
                        "createdAt": 1234567890
                    }
                ],
                "currentPage": 1,
                "totalPages": 1,
                "pageSize": 10,
                "hasNextPage": false,
                "hasPreviousPage": false
            }
        """.trimIndent()

        val client = createMockClient(content = mockResponse)
//        val token = token.getToken()
        repository = BookRepositoryImpl(client, fakeTokenProvider)

        repository.getBooks().collect { result ->
            assertTrue(result is ApiResult.Success)

            val pagination = (result as ApiResult.Success).data
            assertNotNull(pagination)
            assertEquals(1, pagination?.content?.size)

            val book = pagination?.content?.first()
            assertEquals("1", book?.id)
            assertEquals("Test Book", book?.title)
            assertEquals("Author A", book?.author)
            assertEquals("EN", book?.language)
            assertTrue(book?.isPublished == true)
            assertEquals(1234567890L, book?.createdAt)
            assertEquals(1, pagination?.currentPage)
            assertEquals(1L, pagination?.totalPages)
            assertEquals(10, pagination?.pageSize)
        }
    }

    @Test
    fun `Empty book list`() = runBlocking {
        // Verify that the method returns a Flow emitting ApiResult.Success
        // containing a Pagination object with an empty list of books.
        val mockResponse = """
        {
            "content": [],
            "currentPage": 1,
            "totalPages": 1,
            "pageSize": 10,
            "hasNextPage": false,
            "hasPreviousPage": false
        }
    """.trimIndent()

        val client = createMockClient(content = mockResponse)

        repository = BookRepositoryImpl(client, fakeTokenProvider)

        repository.getBooks().collect { result ->
            assertTrue(result is ApiResult.Success)

            val pagination = (result as ApiResult.Success).data
            assertNotNull(pagination)
            assertTrue(pagination?.content?.isEmpty() == true)
            assertEquals(1, pagination?.currentPage)
            assertEquals(1L, pagination?.totalPages)
            assertEquals(10, pagination?.pageSize)
            assertFalse(pagination?.hasNextPage != false)
            assertFalse(pagination?.hasPreviousPage != false)
        }
    }

    @Test
    fun `API returns an error`() = runBlocking {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // when the API call fails, and check the error message.
        val errorMessage = "Internal Server Error"

        val client = createMockClient(
            status = HttpStatusCode.InternalServerError,
            content = errorMessage,
            contentType = "text/plain"
        )

        repository = BookRepositoryImpl(client)

        repository.getBooks().collect { result ->
            assertTrue(result is ApiResult.Error)

            val error = result as ApiResult.Error
            assertTrue(error.errorMessage?.contains("Internal Server Error", ignoreCase = true) == true)
        }
    }

    @Test
    fun `Network connectivity issues`() {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // when there are network connectivity problems, and check the error message.
        // TODO implement test
    }

    @Test
    fun `Base URL is invalid`() {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // when the baseUrl is invalid, potentially resulting in an exception during the API call.
        // TODO implement test
    }

    @Test
    fun `Ktor client configuration error`() {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // if there's an issue with the Ktor client setup preventing successful API calls.
        // TODO implement test
    }

    @Test
    fun `Pagination metadata`() {
        // Verify that the Pagination object within ApiResult.Success contains
        // correct metadata such as total items, page size, and current page number.
        // TODO implement test
    }

    @Test
    fun `Large number of books`() {
        // Test the method with a scenario where the API returns a very large number of books
        // to check for performance or memory-related issues in handling pagination.
        // TODO implement test
    }

    @Test
    fun `API timeout`() {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // when the API request times out.
        // TODO implement test
    }

    @Test
    fun `Unexpected response structure`() {
        // Verify that the method returns a Flow emitting ApiResult.Error
        // if the API returns a response that doesn't conform to the expected Pagination<Book> structure.
        // TODO implement test
    }

    @Test
    fun `Server side errors`() {
        // Test for various HTTP error codes (e.g., 500, 503)
        // and verify that the method correctly handles them and returns a Flow emitting ApiResult.Error.
        // TODO implement test
    }

    @Test
    fun `Concurrent requests`() {
        // Test the behavior of the method when multiple requests are made concurrently
        // to check for thread safety and race conditions within the repository implementation or underlying dependencies.
        // TODO implement test
    }

}