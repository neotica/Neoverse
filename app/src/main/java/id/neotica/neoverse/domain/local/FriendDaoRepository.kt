package id.neotica.neoverse.domain.local

import id.neotica.neoverse.data.local.friend.FriendEntity
import id.neotica.neoverse.data.model.User
import kotlinx.coroutines.flow.Flow

interface FriendDaoRepository {
    fun getFriends(): Flow<List<FriendEntity>>
    suspend fun addFriend(friend: User)
    suspend fun unfriend(friend: String)
    suspend fun addFriendList(friends: List<User>)
    suspend fun clearFriends()
}