package id.neotica.domain.usecase

import id.neotica.domain.session.SessionRepository

class GetUserNameUseCase(private val session: SessionRepository) {
    operator fun invoke(): String = session.getUsername().orEmpty()
}
