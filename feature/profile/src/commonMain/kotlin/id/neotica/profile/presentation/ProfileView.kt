package id.neotica.profile.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.droidcore.component.icon.AlertEnum
import id.neotica.neoverse.utils.privacyPolicy
import id.neotica.neoverse.utils.termsOfService
import id.neotica.res.MR
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.LoadingIndicator
import id.neotica.ui.shared.components.MenuItem
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.image.ImageCoil
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.NegativePrimary
import id.neotica.ui.shared.theme.TransparentText20
import org.koin.compose.viewmodel.koinViewModel

private const val DefaultAvatar =
    "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val toast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current
    val sheetState = rememberModalBottomSheetState()
    var showAccountSettings by remember { mutableStateOf(false) }
    val showLogoutDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.AccountDeleted -> {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is ProfileViewModel.ProfileEvent.Message -> {
                    toastMessage.value = event.text
                    toast.value = true
                }
                is ProfileViewModel.ProfileEvent.Error -> {
                    toastMessage.value = event.text
                    toast.value = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) {
            navController.navigate(Screen.LoginScreen) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (showAccountSettings) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSettings = false },
            sheetState = sheetState,
            containerColor = DarkBackground,
        ) {
            Column(Modifier.padding(16.dp)) {
                MenuItem("Reset Password") {
                    showAccountSettings = false
                    navController.navigate(Screen.ResetPassword)
                }
                MenuItem(
                    "Delete Account",
                    textColor = NegativePrimary,
                ) {
                    showAccountSettings = false
                    navController.navigate(Screen.DeleteAccountScreen)
                }
            }
        }
    }

    if (showLogoutDialog.value) {
        NeoAlert(
            openDialog = showLogoutDialog,
            title = "Do you want to logout?",
            backButton = "No",
            hasIcon = true,
            iconType = AlertEnum.WARNING,
            confirmButton = {
                NeoButton("Yes, logout", negative = true) {
                    viewModel.logout()
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
    }

    BasicScaffold(
        topBarTitle = "Profile",
        horizontalAlignment = Alignment.CenterHorizontally,
        paddingValues = paddingValues,
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = White),
                        onClick = { navController.navigate(Screen.AboutScreen) },
                        onLongClick = {
                            toastMessage.value = "About"
                            toast.value = true
                        },
                    ),
                painter = painterResource(MR.images.ic_info),
                contentDescription = "",
                tint = White,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageCoil(
                size = 100.dp,
                url = DefaultAvatar,
                round = true,
            )
            Spacer(Modifier.padding(8.dp))
            Text(
                text = viewModel.username.ifBlank { "Neoverse user" },
                fontSize = FontSize.Big,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = appVariant.appName,
                fontSize = FontSize.Normal,
                color = TransparentText20,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            MenuItem("Account Settings") {
                showAccountSettings = true
            }
            MenuItem("Privacy Policy") {
                uriHandler.openUri(privacyPolicy)
            }
            MenuItem("Terms of Service") {
                uriHandler.openUri(termsOfService)
            }
            Spacer(Modifier.padding(16.dp))
            MenuItem(
                title = "Logout",
                textColor = NegativePrimary,
                leadingIcon = painterResource(MR.images.ic_logout),
            ) {
                showLogoutDialog.value = true
            }
        }
    }
    if (toast.value) NeoToast(toastMessage.value, toast)
}
