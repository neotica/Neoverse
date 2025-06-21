package id.neotica.world.feat.card

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.routes.WorldScreen
import id.neotica.world.utils.chRaven
import id.neotica.world.utils.ravenDescShort

@Composable
fun RavenCard(navController: NavController) {
    WorldMenuCard(
        title = "Raven",
        desc = ravenDescShort,
        fileName = "ch_raven",
        url = chRaven,
    ) { navController.navigate(WorldScreen.RavenChatScreen) }
}