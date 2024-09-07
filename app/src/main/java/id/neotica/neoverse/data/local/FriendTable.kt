package id.neotica.neoverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FriendEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FriendTable: RoomDatabase() {
    abstract fun friendDao(): FriendDao
}