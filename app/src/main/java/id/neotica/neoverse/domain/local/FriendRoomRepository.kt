package id.neotica.neoverse.domain.local

import id.neotica.neoverse.data.local.FriendEntity
import id.neotica.neoverse.data.model.User
import kotlinx.coroutines.flow.Flow

interface FriendRoomRepository {
    fun getFriends(): Flow<List<FriendEntity>>
    suspend fun addFriend(friend: User)
}