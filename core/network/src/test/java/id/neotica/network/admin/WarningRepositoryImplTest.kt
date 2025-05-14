package id.neotica.network.admin

import id.neotica.domain.ApiResult
import id.neotica.domain.remote.admin.WarningRepository
import id.neotica.network.client.createMockClient
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class WarningRepositoryImplTest {
    private lateinit var client: HttpClient
    private lateinit var repository: WarningRepository
    val fakeTokenProvider = FakeTokenProvider()
    val dummy = WarningDummy()

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getWarnings, Successful API call with valid warnings data`() = runBlocking {
        // Given
        val expectedJson = dummy.warningResponseJson
        val expectedResponse = dummy.warningResponse
        client = createMockClient(
            url = "/admin/warning",
            content = expectedJson
        )
        repository = WarningRepositoryImpl(client, fakeTokenProvider)
        // When
        val result = repository.getWarnings().last()
        println("💀: " + result.errorMessage)
        println(result.data)
        // Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedResponse, successData.content)
    }

    @Test
    fun `newWarning emits loading then success on VALID POST`() = runTest {
        val bookId = dummy.newWarningRequest.bookId
        val request = dummy.newWarningRequest
        val expectedResponse = dummy.newWarningResponse
        val jsonResponse = dummy.postWarningResponseJson

        client = createMockClient(
            method = "POST",
            url = "/admin/warning",
            content = jsonResponse,
            contentType = "application/json"
        )

        repository = WarningRepositoryImpl(client, fakeTokenProvider)

        //When
        val emission = repository.newWarning(
            bookId = bookId.toString(),
            request = request
        ).toList()
        println("💀: " + emission.last().errorMessage)

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission: "+emission.last().data .toString())
        val resultWarning = (emission[1] as ApiResult.Success).data
        assertEquals(expectedResponse, resultWarning)
    }

    @Test
    fun `updateWarning succeed`() = runTest {
        val bookId = dummy.newWarningRequest.bookId
        val request = dummy.putWarningRequest
        val expectedResponse = dummy.putWarningResponse
        val jsonResponse = dummy.putWarningResponseJson

        client = createMockClient(
            method = "PUT",
            url = "/admin/warning/$bookId",
            content = jsonResponse,
            contentType = "application/json"
        )

        repository = WarningRepositoryImpl(client, fakeTokenProvider)

        //When
        val emission = repository.updateWarning(
            bookId = bookId.toString(),
            request = request
        ).toList()

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission put: " + emission.last().data.toString())

        val resultBook = (emission[1] as ApiResult.Success).data
        assertEquals(expectedResponse, resultBook)
    }

    @Test
    fun `deleteWarning succeed`() = runTest {
        val bookId = dummy.newWarningRequest.bookId
        val expectedResponse = dummy.deleteWarningResponseSucceeded
        val jsonResponse = dummy.deleteWarningResponseSucceeded

        client = createMockClient(
            method = "DELETE",
            url = "/admin/warning/$bookId",
            content = jsonResponse,
            contentType = "application/json"
        )

        repository = WarningRepositoryImpl(client, fakeTokenProvider)

        //When
        val emission = repository.deleteWarning(
            id = bookId.toString(),
        ).toList()

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission put: " + emission.last().data.toString())

        val resultBook = (emission[1] as ApiResult.Success).data
        assertEquals(expectedResponse, resultBook)
    }
}