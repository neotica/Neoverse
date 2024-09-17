package id.neotica.database

import androidx.room.Database
import androidx.room.RoomDatabase
import id.neotica.database.dao.CurrentUserDao
import id.neotica.database.dao.FriendDao
import id.neotica.database.entity.FriendEntity
import id.neotica.database.entity.CurrentUserEntity

@Database(
    entities = [FriendEntity::class, CurrentUserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MainDatabase: RoomDatabase() {
    abstract fun friendDao(): FriendDao
    abstract fun currentUserDao(): CurrentUserDao
}