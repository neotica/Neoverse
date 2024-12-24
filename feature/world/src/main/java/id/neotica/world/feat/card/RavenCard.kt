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
import androidx.navigation.NavController
import id.neotica.ui.components.ImagePermanentCache
import id.neotica.ui.navigation.WorldScreen
import id.neotica.ui.theme.DarkPrimaryTransparent80
import id.neotica.ui.theme.FontSize
import id.neotica.world.utils.chRaven
import id.neotica.world.utils.ravenDescShort

@Composable
fun RavenCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors().copy(containerColor = DarkPrimaryTransparent80)
    ) {
        Row(
            modifier = Modifier.clickable { navController.navigate(WorldScreen.RavenChatScreen) }
        ) {
            ImagePermanentCache(
                context = LocalContext.current,
                contentScale = ContentScale.FillWidth,
                fileName = "ch_raven",
                url = chRaven,
                size = 100.dp
            )
            Column(
                Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Raven")
                Text(
                    text = ravenDescShort,
                    fontSize = FontSize.Smaller,
                    fontWeight = FontWeight.Light,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}