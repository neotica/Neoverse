package id.neotica.neoversewear.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Text
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.neoversewear.presentation.navigation.SpacerItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import org.koin.androidx.compose.koinViewModel

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
        item {
            Chip(label = { Text("Home") }, onClick = {
                navController.navigate(WearScreen.HomeScreen.toString())
            })
        }
        item { SpacerItem() }
        item {
            Chip(label = { Text("Bar") }, onClick = {
                navController.navigate(WearScreen.BarScreen.toString())
            })
        }
        item { SpacerItem() }
        item {
            Chip(label = { Text("Profile") }, onClick = {
                navController.navigate(WearScreen.ProfileScreen.toString())
            })
        }

    }
}