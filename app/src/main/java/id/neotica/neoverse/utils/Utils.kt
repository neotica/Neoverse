package id.neotica.neoverse.utils

import java.text.SimpleDateFormat
import java.util.Date

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
    return format.format(date)
}


fun getPartWithUser(combinedRoomName: String, current: String): String {
    // Split the combined room name by " and "
    val parts = combinedRoomName.split(" and ")

    // Find the part that contains the secondUser
    return parts.find { it.contains(current) && it.length == current.length + 2 } ?: "No match found"
}