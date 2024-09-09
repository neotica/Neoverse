package id.neotica.neoverse.domain.local

import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import id.neotica.neoverse.data.remote.model.User
import kotlinx.coroutines.flow.Flow

interface CurrentUserRepository {
    suspend fun saveUser(user: User)
    suspend fun clearCurrent()
    fun getUser(): Flow<List<CurrentUserEntity>>
}