package id.neotica.neoverse.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit
import androidx.core.graphics.scale

fun compressUri(uri: Uri, context: Context): Uri {
    val contentResolver = context.contentResolver
    val bitmap: Bitmap = if (VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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

fun getImageUriFromBitMap(bitMap: Bitmap, context: Context): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg") // Use current timestamp for unique naming
    file.outputStream().use { outputStream ->
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Compress bitmap into JPEG format
    }
    return Uri.fromFile(file)
}

fun isLessThanApi28(): Boolean = VERSION.SDK_INT <= Build.VERSION_CODES.P

fun Bitmap.rotateBitmap(rotationDegrees: Int, isFrontCamera: Boolean = true): Bitmap {
    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
        if (isFrontCamera) {
            postScale(-1f, 1f)
        }
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val aspectRatio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (width > height) {
        newWidth = maxWidth
        newHeight = (maxWidth / aspectRatio).toInt()
    } else {
        newHeight = maxHeight
        newWidth = (maxHeight * aspectRatio).toInt()
    }

    return bitmap.scale(newWidth, newHeight)
}