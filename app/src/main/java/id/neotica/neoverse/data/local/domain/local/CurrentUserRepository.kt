package id.neotica.neoverse.data.local.domain.local

import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import id.neotica.domain.model.User
import kotlinx.coroutines.flow.Flow

interface CurrentUserRepository {
    suspend fun saveUser(user: User)
    suspend fun clearCurrent()
    fun getUser(): Flow<List<CurrentUserEntity>>
}