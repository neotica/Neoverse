package id.neotica.neoverse.presentation.screen.profile

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.User
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.ImageCoil
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.components.SlideInto
import id.neotica.ui.theme.DarkPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<User?>(null) }
    val isLoading by remember { mutableStateOf(false) }
    var accountSettings by remember { mutableStateOf(false) }

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
        paddingValues = paddingValues
    ) {
        LoadingIndicatorBool(isLoading = isLoading) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                user?.let {
                    ImageCoil(url = it.imageUrl)
                    Text(it.username)
                    Text(it.userEmail)
                }

                Spacer(
                    Modifier
                        .padding(0.dp)
                        .weight(1f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                ) {
                    NeoButton("Edit Profile", true) {
                        navController.navigate(Screen.EditProfileScreen(profPic = user?.imageUrl ?: ""))
                    }
                    NeoButton(
                        if (accountSettings)"Account Settings -"
                        else "Account Settings +"
                        , true
                    ) {
                        accountSettings = !accountSettings
                    }
                    SlideInto(accountSettings) {
                        Column(Modifier.padding(horizontal = 8.dp)) {
                            NeoButton("Delete Account", true) {
                                navController.navigate(Screen.DeleteAccountScreen)
                            }
                            NeoButton("Change Password", true) {
//                                viewModel.deleteAccount()
                            }
                        }
                    }
                    NeoButton(
                        "Logout",
                        true
                    ) {
                        viewModel.logout()
                        navController.navigate(RootScreen.HomeNav) {
                            popUpTo(RootScreen.HomeNav) {
                                inclusive = true
                            }
                            launchSingleTop
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            color = DarkPrimary,
                            fontSize = 14.sp,
                            text = "Privacy Policy",
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.PrivacyPolicyScreen)
                            }
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            color = DarkPrimary,
                            fontSize = 14.sp,
                            text = "Terms of Service",
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.TermsOfServiceScreen)
                            }
                        )
                    }
                }
            }
        }
    }
}