package id.neotica.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import id.neotica.auth.login.LoginView
import id.neotica.auth.register.RegisterView
import id.neotica.auth.verify.VerifyEmailView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen
import id.neotica.ui.state.AppVariant

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant
){
    navigation<RootScreen.AuthNav>(
        startDestination = Screen.LoginScreen,
    ) {
        composable<Screen.LoginScreen> {
            LoginView(navController, appVariant)
        }
        composable<Screen.RegisterScreen> {
            RegisterView(navController)
        }
        composable<Screen.VerifyEmailScreen> {
            VerifyEmailView(navController, paddingValues)
        }
    }
}