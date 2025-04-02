package id.neotica.network.utils

import id.neotica.domain.ApiResult
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException

inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Flow<ApiResult<T>> = flow {
    try {
        val result = apiCall()
        emit(ApiResult.Success(result))
    } catch (e: IOException) { // Handles network failures
        emit(ApiResult.Error("Network error: ${e.message}"))
    } catch (e: HttpRequestTimeoutException) { // Handles timeout
        emit(ApiResult.Error("Request timed out: ${e.message}"))
    } catch (e: Exception) { // Handles other unexpected errors
        emit(ApiResult.Error("Unexpected error: ${e.message}"))
    }
}