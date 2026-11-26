package id.neotica.profile.presentation.deleteaccount

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeleteAccountRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: DeleteAccountViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DeleteAccountEvent.AccountDeleted -> navController.navigate(Screen.LoginScreen) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    DeleteAccountView(
        state = state,
        paddingValues = paddingValues,
        onConfirmationChange = viewModel::onConfirmationChange,
        onConfirm = viewModel::deleteAccount,
        onBack = navController::popBackStack,
        onToastDismissed = viewModel::onToastDismissed,
    )
}
