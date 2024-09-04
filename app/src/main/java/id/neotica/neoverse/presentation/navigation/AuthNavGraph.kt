package id.neotica.neoverse.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.neoverse.presentation.screen.auth.login.LoginView
import id.neotica.neoverse.presentation.screen.auth.register.RegisterView
import id.neotica.neoverse.presentation.screen.auth.verify.VerifyEmailView
import id.neotica.neoverse.presentation.screen.profile.ProfileView

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
){
    navigation<RootScreen.AuthNav>(
        startDestination = Screen.ProfileScreen,
    ) {
        composable<Screen.LoginScreen>{
            LoginView(navController, paddingValues = paddingValues)
        }
        composable<Screen.RegisterScreen>{
            RegisterView(navController, paddingValues = paddingValues)
        }
        composable<Screen.ProfileScreen> { ProfileView(navController, paddingValues = paddingValues) }
        composable<Screen.VerifyEmailScreen> {
            VerifyEmailView(navController, paddingValues)
        }
    }
}