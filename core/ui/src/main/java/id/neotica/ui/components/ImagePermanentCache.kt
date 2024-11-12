package id.neotica.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImagePermanentCache(
    modifier: Modifier = Modifier,
    context: Context,
    fileName: String,
    url: String,
    clickable: () -> Unit
) {
    if (ifFileExist(context, fileName)) {
        Box(
            Modifier
                .size(200.dp)
                .clickable { clickable() },
            contentAlignment = Alignment.Center
        ) {
            DisplaySavedImage(fileName)
        }
    } else {
        downloadAndSaveImage(
            context = context,
            url = url,
            fileName = fileName
        )
        ImageCoil(
            url = url,
            size = 200.dp
        ) {
            clickable()
        }
    }
}

@Composable
private fun DisplaySavedImage(fileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap = remember { loadImageFromFilesDir(context, fileName) }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Saved Image",
            modifier = modifier
        )
    }
}

private fun loadImageFromFilesDir(context: Context, fileName: String): Bitmap? {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)
    } else {
        null
    }
}

private fun ifFileExist(context: Context, fileName: String): Boolean {
    val file = File(context.filesDir, fileName)
    return file.exists()
}

private fun downloadAndSaveImage(context: Context, url: String, fileName: String) {
    val imageLoader = Coil.imageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .target { result ->
            val bitmap = (result as? BitmapDrawable)?.bitmap ?: result.toBitmap()
            saveBitmapToFile(context, bitmap, fileName)
        }
        .build()
    imageLoader.enqueue(request)
}

private fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String) {
    val file = File(context.filesDir, fileName) // Create a file in filesDir with the specified file name
    var fileOutputStream: FileOutputStream? = null

    try {
        fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream) // Save as PNG with 100% quality
        fileOutputStream.flush()
        println("Image saved to: ${file.absolutePath}")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fileOutputStream?.close()
    }
}