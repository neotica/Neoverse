package id.neotica.alexandria

import android.app.Application
import id.neotica.alexandria.di.appVariantModule
import id.neotica.alexandria.di.repositoryModules
import id.neotica.alexandria.di.useCaseModules
import id.neotica.alexandria.di.viewModelModules
import id.neotica.database.databaseModule
import id.neotica.network.micro.ktorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AlexandriaApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AlexandriaApp)
            modules(
                ktorModule,
                databaseModule,
                repositoryModules,
                viewModelModules,
                useCaseModules,
                appVariantModule
            )
        }
    }
}