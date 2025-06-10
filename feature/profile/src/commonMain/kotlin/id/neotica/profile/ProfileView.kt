package id.neotica.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.droidcore.component.alert.NeoAlert
import id.neotica.droidcore.component.icon.AlertEnum
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.image.ImageCoil
import id.neotica.ui.shared.components.LoadingIndicatorBool
import id.neotica.ui.shared.components.MenuItem
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.theme.DarkBackground
import id.neotica.ui.shared.theme.FontSize
import id.neotica.ui.shared.theme.NegativePrimary
import id.neotica.ui.shared.theme.TransparentText20
import org.koin.compose.viewmodel.koinViewModel
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.droidcore.component.alert.NeoToast
import id.neotica.rickpository.res.MR
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<User?>(null) }
    val isLoading by remember { mutableStateOf(false) }
    var toast = remember { mutableStateOf(false) }
    var toastMessage = remember { mutableStateOf("") }

    val haptic = LocalHapticFeedback.current

    val sheetState = rememberModalBottomSheetState()
    var showAccountSettingsBottomSheet by remember { mutableStateOf(false) }

    val showLogoutDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) {
            when(appVariant.appName) {
                "Neoverse" -> {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo(RootScreen.HomeNav) {
                            inclusive = true
                        }
                        launchSingleTop
                    }
                }
                "Alexandria" -> {
                    navController.navigate(Screen.LoginScreen) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
                else -> {}
            }

        } else {
            viewModel.currentUser.collect { result ->
//                Log.d("profileView", "result $result")
                when(result) {
                    is ApiResult.Loading -> { /*isUserEmpty = true*/ }
                    is ApiResult.Success -> { user = result.data }
                    is ApiResult.Error -> { /*isUserEmpty = true*/ }
                    null -> { /*isUserEmpty = true*/ }
                }
            }
        }
    }

    if (showAccountSettingsBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSettingsBottomSheet = false },
            sheetState = sheetState,
            containerColor = DarkBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                MenuItem("Reset Password") {
                    navController.navigate(Screen.ResetPassword)
                }
                MenuItem(
                    "Delete Account",
                    textColor = NegativePrimary
                ) {
                    navController.navigate(Screen.DeleteAccountScreen)
                }
            }

        }
    }

    if (showLogoutDialog.value) {
        NeoAlert(
            openDialog = showLogoutDialog,
            title = "Do you want to logout?",
            backButton = "Noo",
            hasIcon = true,
            iconType = AlertEnum.WARNING,
            confirmButton = {
                NeoButton("Yes, logout", negative = true) {
                    when(appVariant.appName) {
                        "Neoverse" -> {
                            viewModel.logout()
                            navController.navigate(Screen.LoginScreen) {
                                popUpTo(RootScreen.HomeNav) {
                                    inclusive = true
                                }
                                launchSingleTop
                            }
                        }
                        "Alexandria" -> {
                            viewModel.logout()
                            navController.navigate(Screen.LoginScreen) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                        else -> {}
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
                        onClick = {
                            navController.navigate(Screen.AboutScreen)
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            toastMessage.value = "About"
                            toast.value = true
                        },
                    ),
                painter = painterResource(MR.images.ic_info),
                contentDescription = "",
                tint = White
            )
        }
    ) {
        LoadingIndicatorBool(isLoading = isLoading) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn {
                    item {
                        user?.let {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ImageCoil(
                                    size = 100.dp,
                                    url = it.imageUrl,
                                    round = true
                                )
                                Spacer(Modifier.padding(8.dp))
                                Text(
                                    text = it.username,
                                    fontSize = FontSize.Big,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = it.userEmail,
                                    fontSize = FontSize.Normal,
                                    color = TransparentText20,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            MenuItem(
                                "Edit Profile",
                            ) {
                                navController.navigate(Screen.EditProfileScreen(profPic = user?.imageUrl ?: ""))
                            }
                            MenuItem(
                                "Account Settings",
                            ) {
                                showAccountSettingsBottomSheet = true
                            }
                            MenuItem(
                                "Privacy Policy",
                            ) {
                                navController.navigate(Screen.PrivacyPolicyScreen)
                            }
                            MenuItem(
                                "Terms of Service",
                            ) {
                                navController.navigate(Screen.TermsOfServiceScreen)
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
                }
            }
        }
    }
    if (toast.value) NeoToast(toastMessage.value .toString(), toast)
}