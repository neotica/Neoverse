package id.neotica.neoverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.neotica.neoverse.data.local.dao.FriendDao
import id.neotica.neoverse.data.local.entity.FriendEntity
import id.neotica.neoverse.data.local.dao.CurrentUserDao
import id.neotica.neoverse.data.local.entity.CurrentUserEntity

@Database(
    entities = [FriendEntity::class, CurrentUserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MainDatabase: RoomDatabase() {
    abstract fun friendDao(): FriendDao
    abstract fun currentUserDao(): CurrentUserDao
}