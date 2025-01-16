package id.neotica.network

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import id.neotica.domain.remote.FirebaseRepository

class FirebaseRepositoryImpl: FirebaseRepository {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun itemLog(screen: String, itemName: String, contentType: String){
        firebaseAnalytics = Firebase.analytics

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
            param(FirebaseAnalytics.Param.ITEM_NAME, itemName)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        }
    }

    override fun eventLog(screen: String, eventName: String, eventResult: String){
        firebaseAnalytics = Firebase.analytics

        firebaseAnalytics.logEvent("app_event") {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
            param("event_name", eventName)
            param("event_result", eventResult)
        }
    }
}