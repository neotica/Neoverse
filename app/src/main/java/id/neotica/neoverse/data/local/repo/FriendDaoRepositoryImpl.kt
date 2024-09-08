package id.neotica.neoverse.data.local.repo

import android.util.Log
import id.neotica.neoverse.data.local.friend.FriendDao
import id.neotica.neoverse.data.local.friend.FriendEntity
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.domain.local.FriendDaoRepository
import id.neotica.neoverse.domain.utils.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FriendDaoRepositoryImpl(
    private val dao: FriendDao,
) : FriendDaoRepository {
    override fun getFriends(): Flow<List<FriendEntity>> = dao.getFriends()

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
        dao.clearFriend()
    }
}