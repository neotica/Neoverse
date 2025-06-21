package id.neotica.neoversewear.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.neoversewear.presentation.components.NeoWChip
import id.neotica.neoversewear.presentation.components.SpacerItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearHomeNavView(navController: NavController, modifier: Modifier = Modifier, viewModel: WHomeViewModel = koinViewModel()) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(WearScreen.LoginScreen.toString()) {
            popUpTo(WearScreen.LoginScreen.toString()) {
                inclusive = true
            }
            launchSingleTop
        }
    }

    BasicWearScreen {
        item { SpacerItem(16.dp) }
        item { NeoWChip("Chat"){ navController.navigate(WearScreen.HomeScreen.toString()) } }
        item { SpacerItem() }
        item {
            NeoWChip("Bar") {
                navController.navigate(WearScreen.BarScreen.toString())
            }
        }
        item { SpacerItem() }
        item {
            NeoWChip("Profile") {
                navController.navigate(WearScreen.ProfileScreen.toString())
            }
        }
        item { SpacerItem(16.dp) }
    }
}