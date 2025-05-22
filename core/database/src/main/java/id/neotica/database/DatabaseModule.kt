package id.neotica.database

import android.content.Context
import androidx.room.Room
import org.koin.dsl.module

fun databaseModule(context: Context) = module {
    factory { get<MainDatabase>().friendDao() }
    factory { get<MainDatabase>().currentUserDao() }
    single {
        Room.databaseBuilder(
            context = context.applicationContext,
            klass = MainDatabase::class.java,
            name = "Friend.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
}