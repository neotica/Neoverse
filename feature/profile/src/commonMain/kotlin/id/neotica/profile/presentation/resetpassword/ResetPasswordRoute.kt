package id.neotica.profile.presentation.resetpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoute(
    navController: NavController,
    viewModel: ResetPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    ResetPasswordView(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onSubmit = viewModel::resetPassword,
        onBack = navController::popBackStack,
        onToastDismissed = viewModel::onToastDismissed,
    )
}
