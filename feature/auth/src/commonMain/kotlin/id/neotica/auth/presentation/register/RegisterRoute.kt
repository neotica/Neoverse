package id.neotica.auth.presentation.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoute(
    navController: NavController,
    viewModel: RegisterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterEvent.Registered -> navController.navigate(
                    Screen.VerifyEmailScreen(event.email)
                )
            }
        }
    }

    RegisterView(
        state = state,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onAgreedChange = viewModel::onAgreedChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSubmit = viewModel::register,
        onBack = navController::popBackStack,
        onPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicyScreen) },
        onTermsOfService = { navController.navigate(Screen.TermsOfServiceScreen) },
        onToastDismissed = viewModel::onToastDismissed,
    )
}
