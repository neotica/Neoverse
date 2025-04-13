package id.neotica.world.feat.card

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.ui.navigation.AlexandriaScreen
import id.neotica.ui.navigation.RootScreen
import id.neotica.world.utils.chLibrary
import id.neotica.world.utils.libraryDesc

@Composable
fun LibraryCard(navController: NavController) {
    WorldMenuCard(
        title = "Library",
        desc = libraryDesc,
        fileName = "ch_library",
        url = chLibrary,
        onLongClick = { navController.navigate(AlexandriaScreen.UserLibraryScreen) }
    ) { navController.navigate(RootScreen.AlexandriaNav) }
}