package id.neotica.neoverse.data.local.repo

import id.neotica.neoverse.data.local.user.CurrentUserDao
import id.neotica.neoverse.data.local.user.CurrentUserEntity
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.domain.utils.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CurrentUserRepositoryImpl(
    private val dao: CurrentUserDao
) {
    suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        val userEnt = Mapper.userResponseToEntity(user)
        dao.saveCurrentUser(userEnt)
    }
    suspend fun clearCurrent() = withContext(Dispatchers.IO) {
        dao.clearUser()
    }

    fun getUser(): Flow<List<CurrentUserEntity>> = dao.currentUser2()
}