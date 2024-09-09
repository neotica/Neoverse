package id.neotica.neoverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.neotica.neoverse.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("select * from friend order by username asc")
    fun getFriends(): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriendList(friends: List<FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriend(friend: FriendEntity)

    @Query("delete from friend where username = :username")
    fun unfriend(username: String)

    @Query("delete from friend")
    fun clearFriend()
}