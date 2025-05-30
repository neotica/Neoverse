package id.neotica.neoverse

import android.app.Application
import id.neotica.neoverse.di.initializeKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKoin(this@MyApp)
    }
}