package id.neotica.auth.data

import id.neotica.domain.ApiResult

actual suspend fun loginResult(
    email: String,
    password: String
): ApiResult<String> {
    TODO("Not yet implemented")
}

actual suspend fun registerResult(
    username: String,
    email: String,
    password: String
): ApiResult<String> {
    TODO("Not yet implemented")
}

actual suspend fun isUsernameExistResult(username: String): ApiResult<Boolean> {
    TODO("Not yet implemented")
}

actual fun deleteAccountResult() {}
actual fun logoutResult() {}
actual fun resetPasswordResult(email: String) {}
actual fun sendEmailVerificationResult() {}
actual fun isUserVerifiedResult(): Boolean {
    TODO("Not yet implemented")
}