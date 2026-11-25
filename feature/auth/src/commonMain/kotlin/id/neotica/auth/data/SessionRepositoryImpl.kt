package id.neotica.auth.data

import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.domain.session.SessionRepository

class SessionRepositoryImpl(
    private val authRepository: AuthRepository,
) : SessionRepository, TokenProvider {

    override fun getUserId(): String? = authRepository.getUserId()

    override fun getUsername(): String? = authRepository.getUsername()

    override fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    override suspend fun getAccessToken(): String? =
        authRepository.getCurrentUserToken().takeIf { it.isNotBlank() }

    override suspend fun refreshAccessToken(): String? = authRepository.refreshToken()

    override suspend fun getToken(): String? = getAccessToken()

    override suspend fun clearSession() = authRepository.logout()

    override suspend fun refreshToken(): String? = refreshAccessToken()
}
