package id.neotica.database

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { get<MainDatabase>().friendDao() }
    factory { get<MainDatabase>().currentUserDao() }
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = MainDatabase::class.java,
            name = "Friend.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}