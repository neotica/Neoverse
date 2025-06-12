package id.neotica.alexandria.domain

import id.neotica.domain.ApiResult
import kotlinx.coroutines.flow.Flow

interface PermissionRepository {
    fun getPermissionList(): Flow<ApiResult<List<String>>>
    suspend fun hasPermission(userName: String): Boolean
}