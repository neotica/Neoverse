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
import id.neotica.neoverse.presentation.screen.profile.deleteaccount.DeleteAccountView
import id.neotica.neoverse.presentation.screen.profile.privacypolicy.PrivacyPolicyView
import id.neotica.neoverse.presentation.screen.profile.termsofsercive.TermsOfServiceView

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
        composable<Screen.PrivacyPolicyScreen> {
            PrivacyPolicyView(navController, paddingValues)
        }
        composable<Screen.TermsOfServiceScreen> {
            TermsOfServiceView(navController, paddingValues)
        }
        composable<Screen.DeleteAccountScreen> {
            DeleteAccountView(navController, paddingValues)
        }
    }
}