package id.neotica.auth.data

import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.neoverse.getCurrentTimeMillis
import id.neotica.neoverse.utils.authBaseUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val tokenStore: TokenStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): ApiResult<String> = try {
        val response = client.get("$authBaseUrl/auth/login") {
            header("Username", username)
            header("Password", password)
        }
        when (response.status) {
            HttpStatusCode.OK -> {
                val session = response.body<AuthSessionDto>()
                tokenStore.save(
                    StoredSession(
                        accessToken = session.token,
                        refreshToken = session.refreshToken,
                        expiresAtSeconds = session.expirationTime
                    )
                )
                val loaded = loadUserInfo(session.token)
                if (loaded) ApiResult.Success("Login successful")
                else {
                    tokenStore.clear()
                    ApiResult.Error("Unable to load account details")
                }
            }
            HttpStatusCode.Unauthorized -> ApiResult.Error("Invalid username or password")
            HttpStatusCode.Forbidden -> ApiResult.Error("EMAIL_NOT_VERIFIED")
            HttpStatusCode.TooManyRequests -> ApiResult.Error("Too many attempts. Try again later.")
            else -> ApiResult.Error(response.errorText())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Login failed")
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String,
    ): ApiResult<String> = try {
        val response = client.post("$authBaseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username = username, email = email, password = password))
        }
        if (response.status == HttpStatusCode.OK) {
            val message = runCatching { response.body<MessageResponse>().message }.getOrNull()
            ApiResult.Success(message ?: "Account created. Check your email for verification.")
        } else {
            ApiResult.Error(response.errorText())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Registration failed")
    }

    override suspend fun resendVerification(email: String): ApiResult<String> = try {
        val response = client.post("$authBaseUrl/auth/resend-verification") {
            header("Email", email)
        }
        if (response.status == HttpStatusCode.OK) {
            ApiResult.Success("Verification email sent")
        } else {
            ApiResult.Error(response.errorText())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to resend verification email")
    }

    override suspend fun requestPasswordReset(email: String): ApiResult<String> = try {
        val response = client.post("$authBaseUrl/auth/forgot-password") {
            header("Email", email)
        }
        if (response.status == HttpStatusCode.OK) {
            ApiResult.Success("If the email exists, a reset link has been sent.")
        } else {
            ApiResult.Error(response.errorText())
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unable to request password reset")
    }

    override suspend fun deleteAccount(): ApiResult<String> {
        val session = tokenStore.load() ?: return ApiResult.Error("Not authenticated")
        val id = session.userId
        if (id.isBlank()) return ApiResult.Error("Unable to resolve account")
        return try {
            val response = client.delete("$authBaseUrl/auth/") {
                header("Id", id)
                header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
            }
            if (response.status.isSuccess()) {
                tokenStore.clear()
                ApiResult.Success("Account deleted")
            } else {
                ApiResult.Error(response.errorText())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unable to delete account")
        }
    }

    override suspend fun logout() {
        tokenStore.clear()
    }

    override suspend fun getCurrentUserToken(): String {
        val session = tokenStore.load() ?: return ""
        if (session.accessToken.isBlank()) return ""
        val now = currentTimeSeconds()
        if (session.expiresAtSeconds > 0 && now >= session.expiresAtSeconds - 60) {
            return refreshToken().orEmpty()
        }
        return session.accessToken
    }

    override suspend fun refreshToken(): String? {
        val session = tokenStore.load() ?: return null
        if (session.refreshToken.isBlank()) return null
        return try {
            val response = client.get("$authBaseUrl/auth/reload") {
                header(HttpHeaders.Authorization, "Bearer ${session.refreshToken}")
            }
            if (response.status == HttpStatusCode.OK) {
                val refreshed = response.body<AuthSessionDto>()
                tokenStore.save(
                    session.copy(
                        accessToken = refreshed.token,
                        refreshToken = refreshed.refreshToken,
                        expiresAtSeconds = refreshed.expirationTime
                    )
                )
                refreshed.token
            } else {
                if (response.status == HttpStatusCode.Unauthorized) tokenStore.clear()
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun isLoggedIn(): Boolean = tokenStore.load()?.accessToken?.isNotBlank() == true

    override fun getUserId(): String? = tokenStore.load()?.userId?.takeIf { it.isNotBlank() }

    override fun getUsername(): String? = tokenStore.load()?.username?.takeIf { it.isNotBlank() }

    private suspend fun loadUserInfo(token: String): Boolean {
        return try {
            val userIdResponse = client.get("$authBaseUrl/auth/user/userid") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            val usernameResponse = client.get("$authBaseUrl/auth/user/username") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            val userId = userIdResponse.bodyAsText().trim()
            val username = usernameResponse.bodyAsText().trim()
            if (userId.isBlank() || username.isBlank()) return false
            val current = tokenStore.load() ?: return false
            tokenStore.save(current.copy(userId = userId, username = username))
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun HttpResponse.errorText(): String {
        val text = runCatching { bodyAsText() }.getOrDefault("")
        return text.ifBlank { "Request failed (${status.value})" }
    }

    private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299
}

private fun currentTimeSeconds(): Long = getCurrentTimeMillis().time / 1000
