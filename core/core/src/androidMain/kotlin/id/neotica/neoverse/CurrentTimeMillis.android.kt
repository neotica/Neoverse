package id.neotica.neoverse

class AndroidCurrentTimeMillis: CurrentTimeMillis {
    override val time: Long = System.currentTimeMillis()
}

actual fun getCurrentTimeMillis(): CurrentTimeMillis = AndroidCurrentTimeMillis()