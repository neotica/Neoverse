package id.neotica.network.dummy

import id.neotica.domain.remote.usecase.TokenProvider

class FakeTokenProvider : TokenProvider {
    override suspend fun getToken(): String? {
        return "dfsdsf"
    }
}