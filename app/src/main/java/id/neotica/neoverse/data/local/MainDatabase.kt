package id.neotica.neoverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.neotica.neoverse.data.local.friend.FriendDao
import id.neotica.neoverse.data.local.friend.FriendEntity
import id.neotica.neoverse.data.local.user.CurrentUserDao
import id.neotica.neoverse.data.local.user.CurrentUserEntity

@Database(
    entities = [FriendEntity::class, CurrentUserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MainDatabase: RoomDatabase() {
    abstract fun friendDao(): FriendDao
    abstract fun currentUserDao(): CurrentUserDao
}