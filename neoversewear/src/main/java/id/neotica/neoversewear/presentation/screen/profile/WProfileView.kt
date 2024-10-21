package id.neotica.neoversewear.presentation.screen.profile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.neoversewear.presentation.components.BasicWearScreen
import id.neotica.neoversewear.presentation.components.NeoWChip
import id.neotica.neoversewear.presentation.navigation.SpacerItem
import id.neotica.neoversewear.presentation.navigation.WearScreen
import id.neotica.ui.components.ImageCoil
import org.koin.androidx.compose.koinViewModel

@Composable
fun WProfileView(navController: NavController, modifier: Modifier = Modifier, viewModel: WProfileViewModel = koinViewModel()) {
    var user by remember { mutableStateOf<User?>(null) }
    val isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(WearScreen.LoginScreen.toString()) {
            popUpTo(WearScreen.HomeNavScreen.toString()) {
                inclusive = true
            }
            launchSingleTop
        } else {
            viewModel.currentUser.collect { result ->
                Log.d("profileView", "result $result")
                when(result) {
                    is ApiResult.Loading -> { /*isUserEmpty = true*/ }
                    is ApiResult.Success -> { user = result.data }
                    is ApiResult.Error -> { /*isUserEmpty = true*/ }
                    null -> { /*isUserEmpty = true*/ }
                }
            }
        }
    }

    BasicWearScreen {
        item { SpacerItem() }
        user?.let {
            item { ImageCoil(url = it.imageUrl, round = true) }
            item { SpacerItem() }
            item { Text(it.username) }
            item { SpacerItem() }
            item { Text(it.userEmail) }
        }
        item { SpacerItem() }
        item {
            NeoWChip("Logout") {
                viewModel.logout()
                navController.navigate(WearScreen.LoginScreen.toString()) {
                    popUpTo(WearScreen.HomeNavScreen.toString()) {
                        inclusive = true
                    }
                    launchSingleTop
                }
            }
        }
    }
}