package id.neotica.auth.data

import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult

class AuthRepositoryImpl: AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<String> = loginResult(email, password)

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): ApiResult<String> = registerResult(username, email, password)

    override suspend fun isUsernameExist(username: String): ApiResult<Boolean> = isUsernameExistResult(username)

    override fun deleteAccount() = deleteAccountResult()

    override fun logout() = logoutResult()

    override fun resetPassword(email: String) = resetPasswordResult(email)

    override fun sendEmailVerification() = sendEmailVerificationResult()
    override fun isUserVerified(): Boolean = isUserVerifiedResult()
}

expect suspend fun loginResult(email: String, password: String): ApiResult<String>
expect suspend fun registerResult(
    username: String,
    email: String,
    password: String
): ApiResult<String>
expect suspend fun isUsernameExistResult(username: String): ApiResult<Boolean>
expect fun deleteAccountResult()
expect fun logoutResult()
expect fun resetPasswordResult(email: String)
expect fun sendEmailVerificationResult()
expect fun isUserVerifiedResult(): Boolean