package id.neotica.domain.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

fun randomUUID(): String = UUID.randomUUID().toString()

fun convertLongToTime(time: Long, minutesOnly: Boolean = false): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    format.timeZone = TimeZone.getDefault()

    return if (minutesOnly) {
        val minutes = SimpleDateFormat("HH:mm", Locale.getDefault())
        minutes.timeZone = TimeZone.getDefault()
        minutes.format(date)
    } else {
        format.format(date)
    }
}