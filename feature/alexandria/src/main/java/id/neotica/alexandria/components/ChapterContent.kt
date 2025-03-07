package id.neotica.alexandria.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.neotica.ui.components.image.ImageCoil
import id.neotica.ui.theme.DarkPrimary

@Composable
fun ChapterContent(
    title: String,
    content: String,
    imageUrl: String? = null
) {
    imageUrl?.let {
        Box(
            Modifier
                .clip(CircleShape)
                .background(DarkPrimary)
                .size(100.dp)
        ) {
            ImageCoil(
                url = it,
                size = 100.dp
            )
        }
    }

    Text(title)
    Text("\n")
    Text(content)
}