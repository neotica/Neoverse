package id.neotica.neoverse.domain.local

import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.neoverse.data.remote.model.User
import kotlinx.coroutines.flow.Flow

interface FriendDaoRepository {
    fun getFriends(): Flow<List<FriendEntity>>
    suspend fun addFriend(friend: User)
    suspend fun unfriend(friend: String)
    suspend fun addFriendList(friends: List<User>)
    suspend fun clearFriends()
}