package id.neotica.auth.login

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import id.neotica.state.AppVariant
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun LoginView(
    navController: NavController,
    appVariant: AppVariant,
    viewModel: LoginViewModel = koinViewModel()
)

