package id.neotica.alexandria

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import id.neotica.alexandria.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this@MyApp)
        initializeKoin {
            androidContext(this@MyApp)
        }
    }
}