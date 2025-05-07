package id.neotica.network

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.admin.WarningRequest
import id.neotica.domain.model.alexandria.admin.WarningResponse
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.MockitoAnnotations

class AlexandriaAdminRepositoryImplTest {

    private lateinit var repository: AlexandriaAdminRepositoryImpl
    private lateinit var client: HttpClient
    val fakeTokenProvider = FakeTokenProvider()

    private fun createMockClient(
        status: HttpStatusCode = HttpStatusCode.OK,
        content: String = "",
        url: String = "",
        contentType: String = "application/json"
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            assertEquals("GET", request.method.value)
            assertEquals(url, request.url.encodedPath)
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
    fun `Successful retrieval of warnings`() = runBlocking {
        // Verify that the method returns a Flow emitting ApiResult.Success
        // containing a Pagination object with a list of books.

        //Given
        val expectedResult = listOf<WarningResponse>(mockWarning)
        client = createMockClient(content = mockResponse, url = "/admin/warning")
        repository = AlexandriaAdminRepositoryImpl(client)

        //When
        val result = repository.getWarnings().toList().last()
        print("admin: " + result.data.toString())
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedResult, result.data?.content)
    }

    @Ignore("Revamped")
    @Test
    fun `Successfully Updated warning`() = runBlocking {
        val id = "8bab7937-c02c-4cd2-a753-e9fcaca52754"
        val requestPath = "/admin/warning/$id"

        val requestBody = """
            {
                "isAppealed":true,
                "isApproved":false
            }
        """.trimIndent()
        val responseBody = """
        {
            "id": "$id",
            "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            "bookTitle": "The Bloody Ages 1",
            "isAppealed": true,
            "isApproved": false,
            "message": "You have psoted too much",
            "version": 1,
            "createdAt": 1745234211269
        }
    """.trimIndent()
        val mockEngine = MockEngine { request ->
            assertEquals("PUT", request.method.value)
            assertEquals(requestPath, request.url.encodedPath)

            // Check the body content
            val body = requestBody.toByteArray().decodeToString()
            assertTrue(body.contains("\"isAppealed\":true"))
            assertTrue(body.contains("\"isApproved\":false"))

            val bodyText = request.body.toByteArray().decodeToString()
            print("🚨 BODY SENT: $bodyText")
            respond(
                content = responseBody,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )

        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        repository = AlexandriaAdminRepositoryImpl(client)

        repository.updateWarning(id, warningRequest = WarningRequest(isAppealed = true, isApproved = false)).collect { result ->
            println("🔍 FLOW RESULT: $result")
            assertTrue(result is ApiResult.Success)

            val warning = (result as ApiResult.Success).data
            assertNotNull(warning)
            assertEquals(id, warning?.id)
            assertEquals(true, warning?.isAppealed)
            assertEquals(false, warning?.isApproved)
            assertEquals("The Bloody Ages 1", warning?.bookTitle)
        }


    }

    companion object {
        val mockWarning = WarningResponse(
            id = "4cfb8a51-ba8a-4825-a7b6-7d352663d1ed",
            bookId = "93c835b6-6545-4570-a3e2-b3b32af6b82d",
            bookTitle = "The Bloody Ages 1",
            isAppealed = false,
            isApproved = false,
            message = "You have psoted too much",
            createdAt = 1745158041796
        )
        val mockResponse = """
            {
                "content": [
                    {
                        "id": "4cfb8a51-ba8a-4825-a7b6-7d352663d1ed",
                        "bookId": "93c835b6-6545-4570-a3e2-b3b32af6b82d",
                        "bookTitle": "The Bloody Ages 1",
                        "isAppealed": false,
                        "isApproved": false,
                        "message": "You have psoted too much",
                        "createdAt": 1745158041796
                    }
                ],
                "currentPage": 1,
                "totalPages": 1,
                "pageSize": 10,
                "hasNextPage": false,
                "hasPreviousPage": false
            }
        """.trimIndent()
    }

}