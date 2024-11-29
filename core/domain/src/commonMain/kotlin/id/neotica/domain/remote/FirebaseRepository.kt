package id.neotica.domain.remote

interface FirebaseRepository {
    fun itemLog(screen: String, itemName: String, contentType: String)
    fun eventLog(screen: String, eventName: String, eventResult: String)
}