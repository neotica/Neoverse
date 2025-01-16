package id.neotica.neoversewear

import android.app.Application
import id.neotica.database.databaseModule
import id.neotica.neoversewear.di.wearModules
import id.neotica.world.di.worldModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyWearApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyWearApp)
            modules(
                wearModules,
                databaseModule,
                worldModules
            )
        }
    }
}