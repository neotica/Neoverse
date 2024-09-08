package id.neotica.neoverse.domain.remote

import id.neotica.neoverse.data.remote.ApiResult

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<String>
    suspend fun register(username: String, email: String, password: String): ApiResult<String>
    suspend fun isUsernameExist(username: String): ApiResult<Boolean>
    fun deleteAccount()
    fun logout()
}