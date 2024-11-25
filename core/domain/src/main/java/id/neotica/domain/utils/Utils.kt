package id.neotica.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

fun randomUUID(): String = UUID.randomUUID().toString()

fun convertLongToTime(time: Long, minutesOnly: Boolean = false): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US)
    return if (minutesOnly) {
        SimpleDateFormat("HH:mm", Locale.US).format(date)
    } else {
        format.format(date)
    }
}

fun convertLongToHour(time: Long, minutesOnly: Boolean = false): String {
    val date = Date(time)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.timeZone = TimeZone.getDefault()

    return if (minutesOnly) {
        val minutesFormat = SimpleDateFormat("mm", Locale.getDefault()) // Only minutes if needed
        minutesFormat.timeZone = TimeZone.getDefault()
        minutesFormat.format(date)
    } else {
        format.format(date)
    }
}