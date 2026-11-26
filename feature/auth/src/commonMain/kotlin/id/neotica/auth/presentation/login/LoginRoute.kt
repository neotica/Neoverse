package id.neotica.auth.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoute(
    navController: NavController,
    appVariant: AppVariant,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                LoginEvent.Success -> {
                    val target = if (appVariant.appName == "Alexandria") {
                        RootScreen.AlexandriaNav
                    } else {
                        RootScreen.HomeNav
                    }
                    navController.navigate(target) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    LoginView(
        state = state,
        appVariant = appVariant,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSubmit = viewModel::login,
        onRegister = { navController.navigate(Screen.RegisterScreen) },
        onForgotPassword = { navController.navigate(Screen.ResetPassword) },
        onToastDismissed = viewModel::onToastDismissed,
    )
}
