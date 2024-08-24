package id.neotica.neoverse.presentation.screen.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel()
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
        }
    }

    BasicScreen(paddingValues = paddingValues) {
        Text("Profile")
        Button(onClick = {
            if (!viewModel.isLoggedIn()) {
                viewModel.logout()
            }
            viewModel.logout()
            navigateAuth(navController)
        }) {
            Text("Logout")
        }
    }

}

fun navigateAuth(navController: NavController) {
    navController.navigate(Screen.LoginScreen)
}