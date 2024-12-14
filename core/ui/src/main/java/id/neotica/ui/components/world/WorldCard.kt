package id.neotica.ui.components.world

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.neotica.ui.components.ImagePermanentCache

@Composable
fun WorldCard(
    title: String,
    fileName: String,
    url: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable {
            onClick()
        }
    ) {
        Box {
            ImagePermanentCache(
                context = LocalContext.current,
                fileName = fileName,
                url = url,
                width = 314.dp,
                height = 184.dp,
                contentScale = ContentScale.Crop,
            )
            Text(
                text = title,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}