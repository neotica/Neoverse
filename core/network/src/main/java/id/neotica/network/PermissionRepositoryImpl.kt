package id.neotica.network

import android.util.Log
import id.neotica.domain.ApiResult
import id.neotica.domain.remote.PermissionRepository
import id.neotica.neoverse.utils.baseUrl
import id.neotica.network.micro.ktorClient
import id.neotica.network.utils.safeApiCall
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PermissionRepositoryImpl: PermissionRepository {
    override fun getPermissionList(): Flow<ApiResult<List<String>>> = safeApiCall {
        ktorClient.get("$baseUrl/permission").body()
    }
    override suspend fun hasPermission(userName: String): Boolean {
        return try {
            getPermissionList().first().data?.contains(userName) == true
        } catch (e: Exception) {
            Log.e("PermissionRepo", "Error checking permission", e)
            false
        }
    }
}