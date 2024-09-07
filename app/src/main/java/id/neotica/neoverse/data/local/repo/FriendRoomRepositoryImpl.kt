package id.neotica.neoverse.data.local.repo

import android.util.Log
import id.neotica.neoverse.data.local.FriendDao
import id.neotica.neoverse.data.local.FriendEntity
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.domain.local.FriendRoomRepository
import id.neotica.neoverse.domain.utils.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FriendRoomRepositoryImpl(
    private val dao: FriendDao
) : FriendRoomRepository {
    override fun getFriends(): Flow<List<FriendEntity>> = dao.getFriends()
    override suspend fun addFriend(friend: User) {
        Log.d("friendRepo", "fun addfriend")
        withContext(Dispatchers.IO) {
            val friendEnt = Mapper.friendResponseToEntity(friend)

            dao.addFriend(friendEnt)
        }
    }
}