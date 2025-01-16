package id.neotica.neoverse

interface CurrentTimeMillis {
    val time: Long
}

expect fun getCurrentTimeMillis(): CurrentTimeMillis