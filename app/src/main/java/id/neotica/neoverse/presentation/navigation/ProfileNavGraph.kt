package id.neotica.neoverse.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import id.neotica.auth.authNavGraph
import id.neotica.neoverse.presentation.screen.camerax.CameraPreviewView
import id.neotica.profile.presentation.about.AboutView
import id.neotica.profile.presentation.accountsettings.AccountSettingView
import id.neotica.profile.presentation.privacypolicy.PrivacyPolicyView
import id.neotica.profile.presentation.ProfileView
import id.neotica.profile.presentation.deleteaccount.DeleteAccountView
import id.neotica.profile.presentation.editprofile.EditProfileView
import id.neotica.profile.presentation.resetpassword.ResetPasswordView
import id.neotica.profile.presentation.termsofservice.TermsOfServiceView
import id.neotica.routes.RootScreen
import id.neotica.routes.Screen
import id.neotica.state.AppVariant

fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    appVariant: AppVariant
){
    navigation<RootScreen.ProfileNav>(
        startDestination = Screen.ProfileScreen,
    ) {
        authNavGraph(navController, paddingValues, appVariant)

        composable<Screen.ProfileScreen> {
            ProfileView(
                navController,
                paddingValues = paddingValues,
                appVariant
            )
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