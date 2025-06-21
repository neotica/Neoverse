package id.neotica.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<MainDatabase>> {
        getDatabaseBuilder(get())
    }
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<MainDatabase> {
    return Room.databaseBuilder<MainDatabase>(
        context = context,
        name = "neoverse.db"
    )
}