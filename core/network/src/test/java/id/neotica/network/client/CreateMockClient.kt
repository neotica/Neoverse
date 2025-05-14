package id.neotica.network.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals

fun createMockClient(
    status: HttpStatusCode = HttpStatusCode.OK,
    method: String? = "GET",
    url: String? = "/alexandria",
    expectedParams: Map<String, String> = emptyMap(),
    content: String = "",
    contentType: String = "application/json"
): HttpClient {
    val mockEngine = MockEngine { request ->
        assertEquals(method, request.method.value)
        assertEquals(url, request.url.encodedPath)
        expectedParams.forEach { (key, value) ->
            val actual = request.url.parameters[key]
            assertEquals("Query param $key mismatch", value, actual)
        }
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