package id.neotica.domain.usecase

import id.neotica.domain.session.SessionRepository

class GetUserIdUseCase(private val session: SessionRepository) {
    operator fun invoke(): String = session.getUserId().orEmpty()
}
