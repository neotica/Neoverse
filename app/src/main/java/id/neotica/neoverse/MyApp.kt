package id.neotica.neoverse

import android.app.Application
import id.neotica.neoverse.di.coreModule
import id.neotica.neoverse.di.domainModules
import id.neotica.neoverse.di.useCaseModules
import id.neotica.neoverse.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(
                coreModule,
                viewModelModules,
                domainModules,
                useCaseModules
            )
        }
    }
}