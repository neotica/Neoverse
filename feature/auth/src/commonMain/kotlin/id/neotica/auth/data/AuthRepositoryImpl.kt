package id.neotica.auth.data

import dev.gitlive.firebase.auth.FirebaseAuth
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult

class AuthRepositoryImpl(
    val auth: FirebaseAuth
): AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): ApiResult<String> = loginResult(email, password, auth)

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): ApiResult<String> = registerResult(username, email, password, auth)

    override suspend fun isUsernameExist(username: String): ApiResult<Boolean> = isUsernameExistResult(username, auth)

    override fun deleteAccount() = deleteAccountResult(auth)

    override fun logout() = logoutResult(auth)

    override fun resetPassword(email: String) = resetPasswordResult(email, auth)

    override fun sendEmailVerification() = sendEmailVerificationResult(auth)
    override fun isUserVerified(): Boolean = isUserVerifiedResult(auth)
    override suspend fun getCurrentUserToken(): String = getCurrentUserTokenResult(auth)
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