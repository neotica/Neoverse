package id.neotica.neoverse

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

class IosCurrentTimeMillis: CurrentTimeMillis {
    override val time: Long = NSDate().timeIntervalSince1970.toLong() * 100
}

actual fun getCurrentTimeMillis(): CurrentTimeMillis = IosCurrentTimeMillis()