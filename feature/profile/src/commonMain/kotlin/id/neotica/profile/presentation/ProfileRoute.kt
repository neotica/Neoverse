package id.neotica.profile.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController
import id.neotica.neoverse.utils.privacyPolicy
import id.neotica.neoverse.utils.termsOfService
import id.neotica.routes.Screen
import id.neotica.state.AppVariant
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoute(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.LoggedOut, ProfileEvent.RequireLogin -> navController.navigate(Screen.LoginScreen) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    ProfileView(
        state = state,
        paddingValues = paddingValues,
        appVariant = appVariant,
        onAccountSettingsVisibilityChange = viewModel::onAccountSettingsVisibilityChange,
        onLogoutDialogVisibilityChange = viewModel::onLogoutDialogVisibilityChange,
        onLogout = viewModel::logout,
        onOpenPrivacyPolicy = { uriHandler.openUri(privacyPolicy) },
        onOpenTermsOfService = { uriHandler.openUri(termsOfService) },
        onOpenAbout = { navController.navigate(Screen.AboutScreen) },
        onOpenResetPassword = { navController.navigate(Screen.ResetPassword) },
        onOpenDeleteAccount = { navController.navigate(Screen.DeleteAccountScreen) },
        onToastDismissed = viewModel::onToastDismissed,
    )
}
