package id.neotica.network.alexandria

import id.neotica.domain.ApiResult
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.network.client.createMockClient
import id.neotica.network.dummy.FakeTokenProvider
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ChapterRepositoryImplTest {
    private lateinit var client: HttpClient
    private lateinit var repository: ChapterRepository
    val fakeTokenProvider = FakeTokenProvider()
    val dummy = ChapterDummy()

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `getChapters, Successful API call with valid chapter data`() = runBlocking {
        // Given
        val expectedResponse = dummy.chapterResponse
        val bookId = dummy.bookResponse.id
        client = createMockClient(
            url = "/alexandria/chapters",
            content = dummy.chaptersResponseJson)
        repository = ChapterRepositoryImpl(client, fakeTokenProvider)
        // When
        val result = repository.getChapters(bookId).last()
        println("💀: " + result.errorMessage)
        println(result.data)
        // Then
        assertTrue(result is ApiResult.Success)
        val successData = (result as ApiResult.Success).data!!
        assertEquals(expectedResponse, successData)
    }
}