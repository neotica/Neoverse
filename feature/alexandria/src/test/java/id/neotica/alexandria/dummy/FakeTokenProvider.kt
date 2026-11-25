package id.neotica.alexandria.dummy

import id.neotica.domain.remote.usecase.TokenProvider

class FakeTokenProvider : TokenProvider {
    override suspend fun getToken(): String = "dfsdsf"

    override suspend fun refreshToken(): String = getToken()
}
