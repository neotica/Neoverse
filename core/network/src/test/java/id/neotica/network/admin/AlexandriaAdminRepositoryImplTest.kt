package id.neotica.network.admin

import id.neotica.domain.ApiResult
import id.neotica.domain.model.alexandria.admin.WarningRequest
import id.neotica.domain.model.alexandria.admin.WarningResponse
import id.neotica.network.AlexandriaAdminRepositoryImpl
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class AlexandriaAdminRepositoryImplTest {

    private lateinit var repository: AlexandriaAdminRepositoryImpl
    private lateinit var client: HttpClient
    val fakeTokenProvider = FakeTokenProvider()
    val dummy = AdminDummy()

    private fun createMockClient(
        status: HttpStatusCode = HttpStatusCode.Companion.OK,
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
        val expectedResult = listOf<WarningResponse>(dummy.warningResponse)
        client = createMockClient(content = dummy.warningResponseJson, url = "/admin/warning")
        repository = AlexandriaAdminRepositoryImpl(client)

        //When
        val result = repository.getWarnings().toList().last()
        print("admin: " + result.data.toString())
        Assert.assertTrue(result is ApiResult.Success)
        Assert.assertEquals(expectedResult, result.data?.content)
    }

    @Test
    fun `putWarning success`() = runTest {
        val warningId = dummy.updatedWarningResponse.id
        val warningRequest = WarningRequest(isAppealed = true, isApproved = false)
        val expectedResponse = dummy.updatedWarningResponse
        val jsonResponse = dummy.updatedWarningJson

        val mockEngine = MockEngine.Companion { request ->
            Assert.assertEquals("PUT", request.method.value)
            Assert.assertEquals("/admin/warning/$warningId", request.url.encodedPath)
            Assert.assertEquals("bearer ${fakeTokenProvider.getToken()}", "bearer dfsdsf")

            respond(
                content = jsonResponse,
                status = HttpStatusCode.Companion.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        repository = AlexandriaAdminRepositoryImpl(client, fakeTokenProvider)


        //When
        val emission = repository.updateWarning(warningId, warningRequest).toList()
        println("result: " + emission.last().data.toString())

        //Then
        Assert.assertTrue(emission.last() is ApiResult.Success)
        val resultBook = (emission.last() as ApiResult.Success).data

        Assert.assertEquals(expectedResponse, resultBook)
    }

}