package id.neotica.database

import android.util.Log
import id.neotica.database.dao.FriendDao
import id.neotica.domain.model.User
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.database.Mapper.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FriendDaoRepositoryImpl(
    private val dao: FriendDao,
) : FriendDaoRepository {
    override fun getFriends(): Flow<List<User>> = dao.getFriends().map {
        it.map { friend ->
            friend.toUser()
        }
    }

    override suspend fun addFriendList(friends: List<User>) = withContext(Dispatchers.IO) {
        val friendsEnt = Mapper.friendsResponseToEntity(friends)
        dao.addFriendList(friendsEnt)
    }

    override suspend fun addFriend(friend: User) {
        Log.d("friendRepo", "fun addfriend")
        withContext(Dispatchers.IO) {
            val friendEnt = Mapper.friendResponseToEntity(friend)
            dao.addFriend(friendEnt)
        }
    }
    override suspend fun unfriend(friend: String) = withContext(Dispatchers.IO) {
        dao.unfriend(friend)
    }

    override suspend fun clearFriends() = withContext(Dispatchers.IO) {
        Log.d("DAO", "clearFriend() called")
        dao.clearFriend()
    }
}