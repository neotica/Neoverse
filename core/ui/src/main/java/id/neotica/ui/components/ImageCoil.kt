package id.neotica.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun ImageCoil(
    url: String,
    size: Dp = 100.dp,
    customModifier: Boolean = false,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
    context: Context = LocalContext.current,
    round: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
) {

    val maxWidth = if (fillMaxWidth) {
        Modifier.fillMaxWidth()
    } else {
        Modifier
    }

    Box(
        maxWidth,
        contentAlignment = contentAlignment
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .build(),
            contentDescription =  "",
            modifier = if (!customModifier) {
                Modifier
                    .size(size)
                    .clip(RoundedCornerShape(if(round) 120.dp else 12.dp))
            } else modifier,
            loading = { LoadingIndicator() },
            contentScale = if(round) ContentScale.FillBounds else ContentScale.Fit
        )
    }
}