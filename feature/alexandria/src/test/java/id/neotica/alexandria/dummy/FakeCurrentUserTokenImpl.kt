package id.neotica.alexandria.dummy

import id.neotica.auth.domain.GetCurrentUserTokenUseCase

class FakeCurrentUserTokenImpl: GetCurrentUserTokenUseCase {
    override suspend fun invoke(): String = "sjdhfli"
}