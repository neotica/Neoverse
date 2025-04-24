package id.neotica.domain.remote

import id.neotica.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface PermissionRepository {
    fun getPermissionList(): Flow<ApiResult<List<String>>>
    suspend fun hasPermission(userName: String): Boolean
}