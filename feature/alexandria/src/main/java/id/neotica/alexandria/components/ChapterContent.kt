package id.neotica.alexandria.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import id.neotica.domain.model.alexandria.legacy.ChapterImage
import id.neotica.ui.components.droidcore.CustomCarousel
import id.neotica.ui.components.image.ImageCoil

@Composable
fun ChapterContent(
    title: String,
    content: String,
    imageList: List<ChapterImage>? = null
) {


    val currentPage = remember { mutableIntStateOf(0) }
    imageList?.let {
        CustomCarousel (
            imageList,
            currentPage,
            true
        ) {
            ImageCoil(it.url)
        }
    }

    Text(content)
}