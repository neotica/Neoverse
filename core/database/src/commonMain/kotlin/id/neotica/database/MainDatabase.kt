package id.neotica.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import id.neotica.database.dao.CurrentUserDao
import id.neotica.database.dao.FriendDao
import id.neotica.database.entity.FriendEntity
import id.neotica.database.entity.CurrentUserEntity
import id.neotica.database.profile.ProfileDao
import id.neotica.database.profile.ProfileEntity

@Database(
    entities = [
        FriendEntity::class,
        CurrentUserEntity::class,
        ProfileEntity::class
    ],
    version = 1,
//    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class MainDatabase: RoomDatabase() {
    abstract fun friendDao(): FriendDao
    abstract fun currentUserDao(): CurrentUserDao
    abstract fun profileDao(): ProfileDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<MainDatabase> {
    override fun initialize(): MainDatabase
}