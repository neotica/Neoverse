package id.neotica.neoverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.neotica.neoverse.data.local.entity.CurrentUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentUser(user: CurrentUserEntity)

    @Query("select * from currentuser limit 1")
    fun currentUser(): Flow<List<CurrentUserEntity>>

    @Query("delete from currentuser")
    suspend fun clearUser()
}