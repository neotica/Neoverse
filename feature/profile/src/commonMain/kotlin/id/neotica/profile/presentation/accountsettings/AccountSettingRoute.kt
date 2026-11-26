package id.neotica.profile.presentation.accountsettings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import id.neotica.routes.Screen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountSettingRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: AccountSettingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    AccountSettingView(
        state = state,
        paddingValues = paddingValues,
        onBack = navController::popBackStack,
        onResetPassword = { navController.navigate(Screen.ResetPassword) },
        onDeleteAccount = { navController.navigate(Screen.DeleteAccountScreen) },
    )
}
