package id.neotica.alexandria.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.neotica.ui.components.image.ImageCoil
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.DarkPrimaryTransparent80

@Composable
fun ChapterItem(
    title: String,
    number: String? = null,
    imageUrl: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier

            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                if (number != null) Text("$number. ")
                Text(title)
            }

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
        }
    }
}