package id.neotica.neoverse

import android.app.Application
import id.neotica.database.databaseModule
import id.neotica.neoverse.di.appVariantModule
import id.neotica.neoverse.di.domainModules
import id.neotica.neoverse.di.repositoryModules
import id.neotica.neoverse.di.useCaseModules
import id.neotica.neoverse.di.viewModelModules
import id.neotica.network.micro.ktorModule
import id.neotica.world.di.worldModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                ktorModule,
                databaseModule,
                repositoryModules,
                viewModelModules,
                domainModules,
                useCaseModules,
                worldModules,
                appVariantModule
            )
        }
    }
}