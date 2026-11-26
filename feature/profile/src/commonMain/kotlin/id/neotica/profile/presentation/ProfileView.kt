package id.neotica.profile.presentation

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.droidcore.component.icon.AlertEnum
import id.neotica.res.MR
import id.neotica.state.AppVariant
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.MenuItem
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.components.ToastHost
import id.neotica.ui.shared.components.image.ImageCoil
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.NegativePrimary
import id.neotica.ui.shared.theme.TransparentText20
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val DefaultAvatar =
    "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    state: ProfileState,
    paddingValues: PaddingValues,
    appVariant: AppVariant,
    onAccountSettingsVisibilityChange: (Boolean) -> Unit,
    onLogoutDialogVisibilityChange: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenResetPassword: () -> Unit,
    onOpenDeleteAccount: () -> Unit,
    onToastDismissed: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    if (state.showAccountSettings) {
        ModalBottomSheet(
            onDismissRequest = { onAccountSettingsVisibilityChange(false) },
            sheetState = sheetState,
            containerColor = DarkBackground,
        ) {
            Column(Modifier.padding(16.dp)) {
                MenuItem("Reset Password") {
                    onAccountSettingsVisibilityChange(false)
                    onOpenResetPassword()
                }
                MenuItem(
                    "Delete Account",
                    textColor = NegativePrimary,
                ) {
                    onAccountSettingsVisibilityChange(false)
                    onOpenDeleteAccount()
                }
            }
        }
    }

    if (state.showLogoutDialog) {
        val openDialog = remember { mutableStateOf(true) }
        NeoAlert(
            openDialog = openDialog,
            title = "Do you want to logout?",
            backButton = "No",
            hasIcon = true,
            iconType = AlertEnum.WARNING,
            confirmButton = {
                NeoButton("Yes, logout", negative = true) {
                    openDialog.value = false
                    onLogout()
                }
            },
        )
        LaunchedEffect(openDialog.value) {
            if (!openDialog.value) onLogoutDialogVisibilityChange(false)
        }
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
                        onClick = onOpenAbout,
                        onLongClick = onOpenAbout,
                    ),
                painter = painterResource(MR.images.ic_info),
                contentDescription = "",
                tint = White,
            )
        },
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
                text = state.username.ifBlank { "Neoverse user" },
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
                .padding(horizontal = 16.dp),
        ) {
            MenuItem("Account Settings") {
                onAccountSettingsVisibilityChange(true)
            }
            MenuItem("Privacy Policy", onClick = onOpenPrivacyPolicy)
            MenuItem("Terms of Service", onClick = onOpenTermsOfService)
            Spacer(Modifier.padding(16.dp))
            MenuItem(
                title = "Logout",
                textColor = NegativePrimary,
                leadingIcon = painterResource(MR.images.ic_logout),
            ) {
                onLogoutDialogVisibilityChange(true)
            }
        }
    }
    ToastHost(state.toastMessage, onToastDismissed)
}

@Preview
@Composable
private fun ProfileViewPreview() {
    ProfileView(
        state = ProfileState(username = "jdoe"),
        paddingValues = PaddingValues(),
        appVariant = AppVariant("Neoverse", MR.images.logo_neoverse),
        onAccountSettingsVisibilityChange = {},
        onLogoutDialogVisibilityChange = {},
        onLogout = {},
        onOpenPrivacyPolicy = {},
        onOpenTermsOfService = {},
        onOpenAbout = {},
        onOpenResetPassword = {},
        onOpenDeleteAccount = {},
        onToastDismissed = {},
    )
}
