package id.neotica.domain.usecase

import id.neotica.domain.session.SessionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetUserIdUseCaseTest {

    @Test
    fun `should return userId when user is logged in`() {
        val useCase = GetUserIdUseCase(FakeSession("1234567"))

        assertEquals("1234567", useCase())
    }

    @Test
    fun `should return empty string when user is not logged in`() {
        val useCase = GetUserIdUseCase(FakeSession(null))

        assertEquals("", useCase())
    }

    private class FakeSession(
        private val userId: String?,
    ) : SessionRepository {
        override fun getUserId(): String? = userId
        override fun getUsername(): String? = null
        override fun isLoggedIn(): Boolean = userId != null
        override suspend fun getAccessToken(): String? = null
        override suspend fun refreshAccessToken(): String? = null
        override suspend fun clearSession() = Unit
    }
}
