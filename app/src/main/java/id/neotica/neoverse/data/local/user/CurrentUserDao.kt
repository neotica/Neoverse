package id.neotica.neoverse.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentUser(user: CurrentUserEntity)

    @Query("select * from currentuser limit 1")
    fun currentUser(): Flow<CurrentUserEntity>

    @Query("select * from currentuser limit 1")
    fun currentUser2(): Flow<List<CurrentUserEntity>>

    @Query("delete from currentuser")
    fun clearUser()
}