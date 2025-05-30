package id.neotica.alexandria

import android.app.Application
import id.neotica.alexandria.di.initializeKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKoin(this@MyApp)
    }
}