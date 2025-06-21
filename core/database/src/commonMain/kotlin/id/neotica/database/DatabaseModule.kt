package id.neotica.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val provideDatabaseModule = module {
    single {
        getRoomDatabase(get()) }
    single { getFriendDatabase(get()) }
    single { getCurrentUserDatabase(get()) }
    single { getProfileDatabase(get()) }
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<MainDatabase>
): MainDatabase = builder
    .addMigrations()
    .fallbackToDestructiveMigrationOnDowngrade(true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

fun getFriendDatabase(db: MainDatabase) = db.friendDao()
fun getCurrentUserDatabase(db: MainDatabase) = db.currentUserDao()
fun getProfileDatabase(db: MainDatabase) = db.profileDao()