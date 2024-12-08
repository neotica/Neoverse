package id.neotica.neoverse.presentation.screen.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.ui.R
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.MenuItem
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.FontSize
import id.neotica.ui.theme.NegativePrimary
import id.neotica.ui.theme.TransparentText20
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<User?>(null) }
    val isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        } else {
            viewModel.currentUser.collect { result ->
                Log.d("profileView", "result $result")
                when(result) {
                    is ApiResult.Loading -> { /*isUserEmpty = true*/ }
                    is ApiResult.Success -> { user = result.data }
                    is ApiResult.Error -> { /*isUserEmpty = true*/ }
                    null -> { /*isUserEmpty = true*/ }
                }
            }
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
                        onClick = {
                            navController.navigate(Screen.AboutScreen)
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            Toast
                                .makeText(context, "About", Toast.LENGTH_SHORT)
                                .show()
                        },
                    ),
                imageVector = ImageVector.vectorResource(R.drawable.ic_info),
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
                                Modifier.fillMaxWidth()
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

//                        Spacer(Modifier.padding(16.dp))

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
                                navController.navigate(Screen.AccountSettingScreen)
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
                                "Logout",
                                textColor = NegativePrimary
                            ) {
                                viewModel.logout()
                                navController.navigate(Screen.LoginScreen) {
                                    popUpTo(RootScreen.HomeNav) {
                                        inclusive = true
                                    }
                                    launchSingleTop
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}