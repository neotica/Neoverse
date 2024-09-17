package id.neotica.neoverse.data.local.repo

import id.neotica.neoverse.data.local.dao.CurrentUserDao
import id.neotica.domain.model.User
import id.neotica.neoverse.data.local.domain.local.CurrentUserRepository
import id.neotica.neoverse.data.local.Mapper
import id.neotica.neoverse.data.local.Mapper.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CurrentUserRepositoryImpl(
    private val dao: CurrentUserDao,
): CurrentUserRepository {

    override suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        val userEnt = Mapper.userResponseToEntity(user)
        dao.saveCurrentUser(userEnt)
    }
    override suspend fun clearCurrent() = withContext(Dispatchers.IO) {
        dao.clearUser()
    }

    override fun getUser(): Flow<List<User>> {
        return dao.currentUser().map { users ->
            users.map { user ->
                user.toUser()
            }
        }
    }
}