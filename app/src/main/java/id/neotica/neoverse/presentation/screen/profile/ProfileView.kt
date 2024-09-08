package id.neotica.neoverse.presentation.screen.profile

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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import id.neotica.neoverse.data.local.user.CurrentUserEntity
import id.neotica.neoverse.data.remote.ApiResult
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen
import id.neotica.neoverse.presentation.ui.components.BasicScaffold
import id.neotica.neoverse.presentation.ui.components.ImageCoil
import id.neotica.neoverse.presentation.ui.components.LoadingIndicatorBool
import id.neotica.neoverse.presentation.ui.components.NeoButton
import id.neotica.neoverse.presentation.ui.components.SlideInto
import id.neotica.neoverse.presentation.ui.theme.DarkPrimary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileView(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ProfileViewModel = koinViewModel()
) {
    var user by remember { mutableStateOf<CurrentUserEntity?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var accountSettings by remember { mutableStateOf(false) }
    var isUserEmpty by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!viewModel.isLoggedIn()) navController.navigate(Screen.LoginScreen) {
            popUpTo(RootScreen.HomeNav) {
                inclusive = true
            }
            launchSingleTop
        } else {
            viewModel.viewModelScope.launch {
                val currentUser = viewModel.getUserRoom()
                currentUser.collect {
                    if (!it.isEmpty()) {
                        user = it[0]
                    } else {
                        viewModel.getUser()
                        isUserEmpty = true
                    }
                }
            }
        }
    }

    LaunchedEffect(isUserEmpty) {
        if (isUserEmpty) {
            viewModel.user.collect { result ->
                when (result) {
                    is ApiResult.Loading -> isLoading = true
                    is ApiResult.Success -> {
                        isLoading = false
                        viewModel.saveUserRoom(result.data!!)
                    }

                    is ApiResult.Error -> {
                        isLoading = false
                    }
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
                    Text(it.email)
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
//                        viewModel.deleteAccount()
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