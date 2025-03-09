package id.neotica.alexandria.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.neotica.domain.model.alexandria.ChapterImage
import id.neotica.ui.components.droidcore.CustomCarousel
import id.neotica.ui.components.image.ImageCoil
import id.neotica.ui.theme.FontSize

@Composable
fun ChapterContent(
    title: String,
    content: String,
    imageList: List<ChapterImage>? = null
) {

    Text(
        text = title,
        fontSize = FontSize.Medium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
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