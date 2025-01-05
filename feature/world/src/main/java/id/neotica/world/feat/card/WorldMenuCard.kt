package id.neotica.world.feat.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.neotica.ui.components.image.ImagePermanentCache
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.FontSize

@Composable
fun WorldMenuCard(
    title: String,
    desc: String,
    fileName: String,
    url: String,

    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
    ) {
        Row(
            modifier = Modifier.clickable { onClick() }
        ) {
            ImagePermanentCache(
                context = LocalContext.current,
                contentScale = ContentScale.FillWidth,
                fileName = fileName,
                url = url,
                size = 100.dp
            )
            Column(
                Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(title)
                Text(
                    text = desc,
                    fontSize = FontSize.Smaller,
                    fontWeight = FontWeight.Light,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}