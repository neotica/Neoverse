package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.neotica.neoverse.presentation.screen.auth.login.LoginView
import id.neotica.neoverse.presentation.screen.auth.register.RegisterView
import id.neotica.neoverse.presentation.screen.auth.verify.VerifyEmailView
import id.neotica.neoverse.presentation.screen.camerax.CameraPreviewView
import id.neotica.neoverse.presentation.screen.profile.ProfileView
import id.neotica.neoverse.presentation.screen.profile.about.AboutView
import id.neotica.neoverse.presentation.screen.profile.accountsettings.AccountSettingView
import id.neotica.neoverse.presentation.screen.profile.deleteaccount.DeleteAccountView
import id.neotica.neoverse.presentation.screen.profile.editprofile.EditProfileView
import id.neotica.neoverse.presentation.screen.profile.privacypolicy.PrivacyPolicyView
import id.neotica.neoverse.presentation.screen.profile.resetpassword.ResetPasswordView
import id.neotica.neoverse.presentation.screen.profile.termsofsercive.TermsOfServiceView
import id.neotica.ui.navigation.RootScreen
import id.neotica.ui.navigation.Screen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    paddingValues: PaddingValues
){
    navigation<RootScreen.AuthNav>(
        startDestination = Screen.ProfileScreen,
    ) {
        composable<Screen.LoginScreen> {
            LoginView(navController)
        }
        composable<Screen.RegisterScreen> {
            RegisterView(navController)
        }
        composable<Screen.ProfileScreen> { ProfileView(navController, paddingValues = paddingValues) }
        composable<Screen.VerifyEmailScreen> {
            VerifyEmailView(navController, paddingValues)
        }
        composable<Screen.PrivacyPolicyScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            PrivacyPolicyView(navController)
        }
        composable<Screen.TermsOfServiceScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            TermsOfServiceView(navController)
        }
        composable<Screen.AccountSettingScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            AccountSettingView(navController, paddingValues)
        }
        composable<Screen.DeleteAccountScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            DeleteAccountView(navController, paddingValues)
        }
        composable<Screen.EditProfileScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val args = it.toRoute<Screen.EditProfileScreen>()
            EditProfileView(navController, paddingValues, args = args)
        }
        composable<Screen.CameraPreviewScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            CameraPreviewView(navController)
        }
        composable<Screen.AboutScreen>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            AboutView(navController)
        }
        composable<Screen.ResetPassword>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            ResetPasswordView(navController)
        }
    }
}