package id.neotica.neoverse

import android.app.Application
import id.neotica.neoverse.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(this@MyApp) {
            androidContext(this@MyApp)
        }
    }
}
