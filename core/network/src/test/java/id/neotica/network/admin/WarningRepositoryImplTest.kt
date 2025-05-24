package id.neotica.network.admin

import id.neotica.domain.ApiResult
import id.neotica.domain.remote.admin.WarningRepository
import id.neotica.network.client.createMockClient
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
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

    /**
     * Test case to verify the behavior of `getWarnings` when the API call is successful
     * and returns valid warnings data.
     *
     * This test mocks a successful API response with dummy warning data.
     * It then calls `getWarnings` and asserts that the result is an [ApiResult.Success]
     * and the data matches the expected warning response.
     */
    @Test
    fun `getWarnings, Successful API call with valid warnings data`() = runBlocking {
        // Given
        val expectedJson = dummy.warningsResponseJson
        val expectedResponse = dummy.warningsResponse
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

    /**
     * Test case to verify the successful retrieval of a warning by its ID.
     *
     * This test mocks a successful API response for a specific warning ID.
     * It then calls `getWarning` with that ID and asserts that:
     * 1. The result is an [ApiResult.Success].
     * 2. The data within the [ApiResult.Success] matches the expected warning response.
     */
    @Test
    fun `getWarningById success`() = runBlocking {
        val expectedResponse = dummy.warningResponse
        client = createMockClient(
            url = "/admin/warning/${expectedResponse.id}",
            content = dummy.warningResponseJson
        )
        repository = WarningRepositoryImpl(client, fakeTokenProvider)
        //When
        val result = repository.getWarning(expectedResponse.id).last()
        println("💀: " + result.errorMessage)
        println("Result getwarningbyid: "+result.data)
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data
        assertEquals(expectedResponse, successData)
    }

    /**
     * Test case for `newWarning` function when a valid POST request is made.
     *
     * This test verifies the following:
     * 1. The function emits a `ApiResult.Loading` state initially.
     * 2. Upon successful API call, it emits an `ApiResult.Success` state.
     * 3. The data within the `ApiResult.Success` state matches the expected response.
     *
     * It sets up a mock HTTP client to simulate a successful POST request to the "/admin/warning" endpoint.
     * The response from the mock client is a predefined JSON string representing a successful warning creation.
     *
     * The test then calls `repository.newWarning` with valid `bookId` and `request` data.
     * It collects all emitted values from the resulting Flow.
     *
     * Finally, it asserts that:
     * - The last emitted value is an instance of `ApiResult.Success`.
     * - The data within the second emitted value (which should be the `ApiResult.Success` after `ApiResult.Loading`)
     *   is equal to the `expectedResponse`.
     */
    @Test
    fun `newWarning emits loading then success on VALID POST`() = runTest {
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
            request = request
        ).toList()
        println("💀: " + emission.last().errorMessage)

        //Then
        assertTrue(emission.last() is ApiResult.Success)
        println("emission: "+emission.last().data .toString())
        val resultWarning = (emission[1] as ApiResult.Success).data
        assertEquals(expectedResponse, resultWarning)
    }

    /**
     * Test case to verify that `newWarning` correctly handles a scenario
     * where a warning for a specific book already exists in the system.
     *
     * This test simulates a POST request to the `/admin/warning` endpoint
     * which returns an HTTP 409 Conflict status code, indicating that the
     * warning could not be created because one already exists for the given book.
     *
     * The test then asserts:
     * 1. The last emission from the `newWarning` flow is an `ApiResult.Error`.
     * 2. The `errorMessage` within the `ApiResult.Error` matches the expected error
     *    message for this scenario.
     */
    @Test
    fun `newWarning succeed, warning for book already exist`() = runTest {
        val request = dummy.newWarningRequest
        val expectedResponse = dummy.postWarningAlreadyExistResponse
        val jsonResponse = dummy.postWarningAlreadyExistResponseJson

        client = createMockClient(
            method = "POST",
            status = HttpStatusCode.Conflict,
            url = "/admin/warning",
            content = jsonResponse,
            contentType = "application/json"
        )

        repository = WarningRepositoryImpl(client, fakeTokenProvider)

        //When
        val emission = repository.newWarning(
            request = request
        ).toList()
        println("💀: " + emission.last().errorMessage)

        //Then
        assertTrue(emission.last() is ApiResult.Error)
        println("emission: "+emission.last().errorMessage .toString())
        val resultWarning = (emission[1] as ApiResult.Error).errorMessage
        assertEquals(expectedResponse, resultWarning)
    }

    /**
     * Test case for `updateWarning` function when the API call is successful.
     *
     * This test verifies the following:
     * - The function emits [ApiResult.Loading] initially.
     * - The function emits [ApiResult.Success] upon successful API response.
     * - The data within the [ApiResult.Success] matches the expected response.
     */
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

    /**
     * Test case to verify that deleting a warning succeeds.
     *
     * This test mocks a successful API call to delete a warning.
     * It sets up a mock HTTP client to return a predefined successful response.
     * Then, it calls the `deleteWarning` method of the repository.
     * Finally, it asserts that the emitted result is `ApiResult.Success` and
     * that the data within the success result matches the expected successful response.
     */
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

    /**
     * Test case for deleting a warning that is not found.
     *
     * This test verifies that the `deleteWarning` function in the `WarningRepository`
     * correctly handles the scenario where the warning to be deleted does not exist.
     * It mocks an HTTP DELETE request to `/admin/warning/{bookId}` that returns a 404 Not Found status.
     * The test asserts that the emitted result is an `ApiResult.Error` and that the error message
     * matches the expected error response for a not found warning.
     */
    @Test
    fun `deleteWarning not found`() = runTest {
        val bookId = dummy.newWarningRequest.bookId
        val expectedResponse = dummy.deleteWarningResponseNotFound
        val jsonResponse = dummy.deleteWarningResponseNotFoundResponseJson

        client = createMockClient(
            status = HttpStatusCode.NotFound,
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
        println("emission delete error: " + emission.last().errorMessage.toString())
        //Then
        assertTrue(emission.last() is ApiResult.Error)

        val resultBook = (emission[1] as ApiResult.Error).errorMessage
        assertEquals(expectedResponse, resultBook)
    }
}