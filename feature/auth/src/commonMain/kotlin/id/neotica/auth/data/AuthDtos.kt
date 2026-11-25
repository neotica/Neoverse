package id.neotica.auth.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthSessionDto(
    val token: String = "",
    val refreshToken: String = "",
    val expirationTime: Long = 0L
)

@Serializable
internal data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
internal data class MessageResponse(
    val message: String? = null,
    val email: String? = null
)
