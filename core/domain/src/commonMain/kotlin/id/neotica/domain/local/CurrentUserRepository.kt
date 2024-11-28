package id.neotica.domain.local

import id.neotica.domain.model.User
import kotlinx.coroutines.flow.Flow

interface CurrentUserRepository {
    suspend fun saveUser(user: User)
    suspend fun clearCurrent()
    fun getUser(): Flow<List<User>>
}