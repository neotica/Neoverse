package id.neotica.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun platformModule(): Module = module {
    single<RoomDatabase.Builder<MainDatabase>> { getDatabaseBuilder() }
}

fun getDatabaseBuilder(): RoomDatabase.Builder<MainDatabase> {
    val dbFilePath = documentDirectory() + "/neoverse.db"
    return Room.databaseBuilder<MainDatabase>(dbFilePath)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val docDir = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )

    return requireNotNull(docDir?.path)
}
