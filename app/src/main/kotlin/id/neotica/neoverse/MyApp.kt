package id.neotica.neoverse

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import id.neotica.neoverse.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(this@MyApp)
        initializeKoin(this@MyApp) {
            androidContext(this@MyApp)
        }
    }
}