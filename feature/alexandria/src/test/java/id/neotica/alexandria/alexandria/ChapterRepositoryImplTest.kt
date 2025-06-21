package id.neotica.alexandria.alexandria

import id.neotica.alexandria.client.createMockClient
import id.neotica.alexandria.data.ChapterRepositoryImpl
import id.neotica.alexandria.domain.ChapterRepository
import id.neotica.alexandria.dummy.FakeCurrentUserTokenImpl
import id.neotica.auth.domain.GetCurrentUserTokenUseCase
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
    private lateinit var currentUserTokenUseCase: GetCurrentUserTokenUseCase
    private lateinit var repository: ChapterRepository
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
        currentUserTokenUseCase = FakeCurrentUserTokenImpl()
        repository = ChapterRepositoryImpl(client, currentUserTokenUseCase)
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