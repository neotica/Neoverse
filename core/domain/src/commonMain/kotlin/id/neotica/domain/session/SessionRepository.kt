package id.neotica.domain.session

interface SessionRepository {
    fun getUserId(): String?
    fun getUsername(): String?
    fun isLoggedIn(): Boolean
    suspend fun getAccessToken(): String?
    suspend fun refreshAccessToken(): String?
    suspend fun clearSession()
}
