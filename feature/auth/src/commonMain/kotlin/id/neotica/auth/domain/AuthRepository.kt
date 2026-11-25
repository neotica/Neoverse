package id.neotica.auth.domain

import id.neotica.domain.ApiResult

interface AuthRepository {
    suspend fun login(username: String, password: String): ApiResult<String>
    suspend fun register(username: String, email: String, password: String): ApiResult<String>
    suspend fun resendVerification(email: String): ApiResult<String>
    suspend fun requestPasswordReset(email: String): ApiResult<String>
    suspend fun deleteAccount(): ApiResult<String>
    suspend fun logout()
    suspend fun getCurrentUserToken(): String
    suspend fun refreshToken(): String?
    fun isLoggedIn(): Boolean
    fun getUserId(): String?
    fun getUsername(): String?
}
