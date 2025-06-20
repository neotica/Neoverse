package id.neotica.auth.data

import co.touchlab.kermit.Logger
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import id.neotica.domain.NeoUser
import id.neotica.ktor.utils.safeApiCallNew
import id.neotica.neoverse.getCurrentTimeMillis
import id.neotica.neoverse.utils.baseUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    val auth: FirebaseAuth,
    private val client: HttpClient,
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): ApiResult<String> = loginResult(email, password, auth)

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): ApiResult<String> {
        try {
            val registerResult = auth.createUserWithEmailAndPassword(email, password)
            if (registerResult.user != null) {
                val currentUser = auth.currentUser!!

                currentUser.sendEmailVerification()

                Logger.i { "neouser auth: ${auth.currentUser!!.uid}" }

                // Save user data in Firestore
                val post = client.post("$baseUrl/user") {
                    headers.append("Content-Type", "application/json")
                    setBody(
                        NeoUser(
                            firebaseId = auth.currentUser!!.uid.toString(),
                            username = username,
                            email = email,
                            displayName = "",
                            createdAt = getCurrentTimeMillis().time
                        )
                    )
                }
                post

                return ApiResult.Success("Registration successful")
            } else {
                Logger.e { "NeoUser error: error register" }
                return ApiResult.Error("Registration failed: Unknown error")
            }
        } catch (e: FirebaseAuthException) {
            Logger.e { "Authentication error: error register" }
            return ApiResult.Error("Authentication Error: ${e.message}")
        } catch (e: FirebaseFirestoreException) {
            Logger.e { "Firestore unexpected error: error register" }
            return ApiResult.Error("Firestore Error: ${e.message}")
        } catch (e: Exception) {
            Logger.e { "NeoUser unexpected error: error register" }
            return ApiResult.Error("Unexpected Error: ${e.message}")
        }
    } //registerResult(username, email, password, auth)

    fun saveUser(user: NeoUser) = safeApiCallNew<NeoUser> {
        client.post("$baseUrl/user") {
            headers.append("Content-Type", "application/json")
            setBody(user)
        }
    }

    override suspend fun isUsernameExist(username: String): ApiResult<Boolean> = isUsernameExistResult(username, auth)

    override fun deleteAccount() = deleteAccountResult(auth)

    override fun logout() = logoutResult(auth)

    override fun resetPassword(email: String) = resetPasswordResult(email, auth)

    override fun sendEmailVerification() = sendEmailVerificationResult(auth)
    override fun isUserVerified(): Boolean = isUserVerifiedResult(auth)
    override suspend fun getCurrentUserToken(): String = getCurrentUserTokenResult(auth)
    override fun isLoggedIn() = isLoggedInResult(auth)
    override fun getUserId() = auth.currentUser?.uid
}

expect suspend fun loginResult(email: String, password: String, authKmp: FirebaseAuth): ApiResult<String>
expect suspend fun registerResult(
    username: String,
    email: String,
    password: String,
    authKmp: FirebaseAuth
): ApiResult<String>
expect suspend fun isUsernameExistResult(username: String, authKmp: FirebaseAuth): ApiResult<Boolean>
expect fun deleteAccountResult(authKmp: FirebaseAuth)
expect fun logoutResult(authKmp: FirebaseAuth)
expect fun resetPasswordResult(email: String, authKmp: FirebaseAuth)
expect fun sendEmailVerificationResult(authKmp: FirebaseAuth)
expect fun isUserVerifiedResult(authKmp: FirebaseAuth): Boolean
expect suspend fun getCurrentUserTokenResult(authKmp: FirebaseAuth): String
expect fun isLoggedInResult(authKmp: FirebaseAuth): Boolean