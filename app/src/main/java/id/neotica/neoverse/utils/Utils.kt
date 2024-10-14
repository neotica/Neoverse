package id.neotica.neoverse.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
    val format = SimpleDateFormat("HH:mm", Locale.US)
    return if (minutesOnly) {
        SimpleDateFormat("HH:mm", Locale.US).format(date)
    } else {
        format.format(date)
    }
}

fun compressUri(uri: Uri, context: Context): Uri {
    val contentResolver = context.contentResolver
    val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

    val byteArray = outputStream.toByteArray()

    val compressedImageUri = getImageUriFromByteArray(byteArray, context)
    return  compressedImageUri
}

fun getImageUriFromByteArray(byteArray: ByteArray, context: Context): Uri {
    val file = File(context.cacheDir, "${TimeUnit.DAYS}.jpg")
    file.writeBytes(byteArray)
    return Uri.fromFile(file)
}