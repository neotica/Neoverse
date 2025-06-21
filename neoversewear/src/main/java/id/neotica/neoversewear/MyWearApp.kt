package id.neotica.neoversewear

import android.app.Application
import id.neotica.neoversewear.di.initializeKoin

class MyWearApp: Application() {
    override fun onCreate() {
        super.onCreate()

        initializeKoin(this@MyWearApp)
    }
}