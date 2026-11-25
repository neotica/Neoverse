package id.neotica.auth

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.neotica.auth.presentation.login.LoginView
import id.neotica.auth.presentation.register.RegisterView
import id.neotica.auth.verify.VerifyEmailView
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant
) {
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
            val args = it.toRoute<Screen.VerifyEmailScreen>()
            VerifyEmailView(navController, paddingValues, args.email)
        }
    }
}
