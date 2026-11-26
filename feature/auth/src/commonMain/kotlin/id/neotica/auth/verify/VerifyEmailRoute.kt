package id.neotica.auth.verify

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VerifyEmailRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    email: String,
    viewModel: VerifyEmailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(email) {
        viewModel.setEmail(email)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                VerifyEmailEvent.LoggedOut -> navController.navigate(Screen.LoginScreen) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    VerifyEmailView(
        state = state,
        paddingValues = paddingValues,
        onResend = viewModel::resend,
        onVerified = viewModel::logout,
        onToastDismissed = viewModel::onToastDismissed,
    )
}
