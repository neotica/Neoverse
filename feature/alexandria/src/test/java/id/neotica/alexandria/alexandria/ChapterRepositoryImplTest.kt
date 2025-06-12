package id.neotica.alexandria.alexandria

import id.neotica.alexandria.client.createMockClient
import id.neotica.alexandria.data.ChapterRepositoryImpl
import id.neotica.alexandria.domain.ChapterRepository
import id.neotica.alexandria.dummy.FakeTokenProvider
import id.neotica.auth.data.AuthRepositoryImpl
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
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
    private lateinit var authRepo: AuthRepository
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
        repository = ChapterRepositoryImpl(client, authRepo)
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