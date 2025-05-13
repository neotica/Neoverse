package id.neotica.network.utils

import id.neotica.domain.ApiResult
import id.neotica.domain.model.error.NotFoundResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/*inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Flow<ApiResult<T>> = flow {
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
}*/

inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading<T>())
    val result = apiCall()
    emit(ApiResult.Success(result))
}.catch { e ->
    emit(ApiResult.Error<T>(e.message ?: "Unknown error"))
}

inline fun <reified T: Any> safeApiCallNew(
    crossinline apiCall: suspend () -> HttpResponse
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading<T>())
    val response = apiCall()
    if (response.status.isSuccess()) {
        val data = response.body<T>()
        emit(ApiResult.Success(data))
    } else {
        val errorBody = response.bodyAsText()
        val errorMessage = try {
            Json.decodeFromString<NotFoundResponse>(errorBody).error
        } catch (e: Exception) {
            "Unexpected error: ${response.status} $e"
        }
        emit(ApiResult.Error<T>(errorMessage))
    }
}.catch { e ->
    emit(ApiResult.Error<T>(e.message ?: "Unknown error"))
}