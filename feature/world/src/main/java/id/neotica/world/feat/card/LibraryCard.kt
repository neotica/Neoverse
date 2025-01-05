package id.neotica.world.feat.card

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.ui.navigation.WorldScreen
import id.neotica.world.utils.chLibrary
import id.neotica.world.utils.libraryDesc

@Composable
fun LibraryCard(navController: NavController) {
    WorldMenuCard(
        title = "Library",
        desc = libraryDesc,
        fileName = "ch_library",
        url = chLibrary,
    ) { navController.navigate(WorldScreen.LibraryScreen) }
}