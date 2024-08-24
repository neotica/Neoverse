package id.neotica.neoverse.presentation.screen.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScreen
import id.neotica.neoverse.presentation.ui.components.ImageCoil
import id.neotica.neoverse.presentation.ui.components.LoadingIndicatorBool
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
        } else {
            viewModel.getUser()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.user.collect { result ->
            when(result) {
                is ApiResult.Loading -> isLoading = true
                is ApiResult.Success -> {
                    isLoading = false
                    user = result.data
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    BasicScreen(paddingValues = paddingValues, horizontalAlign = Alignment.CenterHorizontally) {
        LoadingIndicatorBool(isLoading = isLoading) {

            Text("Profile")

            user?.let {
                ImageCoil(url = it.imageUrl.toString())
                Text(it.username.toString())
                Text(it.userEmail.toString())
            }
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

}

fun navigateAuth(navController: NavController) {
    navController.navigate(Screen.LoginScreen)
}