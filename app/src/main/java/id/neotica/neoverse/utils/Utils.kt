package id.neotica.neoverse.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

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