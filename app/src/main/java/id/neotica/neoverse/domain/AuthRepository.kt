package id.neotica.neoverse.domain

import id.neotica.neoverse.data.remote.ApiResult

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<String>
    suspend fun register(username: String, email: String, password: String): ApiResult<String>
    fun logout()
}